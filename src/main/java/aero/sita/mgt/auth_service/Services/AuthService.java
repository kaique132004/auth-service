package aero.sita.mgt.auth_service.Services;

import aero.sita.mgt.auth_service.Schemas.DTO.*;
import aero.sita.mgt.auth_service.Schemas.Entitys.*;
import aero.sita.mgt.auth_service.Schemas.UserMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static aero.sita.mgt.auth_service.Services.JwtService.EXPIRATION_TIME;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private UserPermissionRepository permissionRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private final LogService logService;


    @Scheduled(cron = "0 0 0 * * *")
    public void disableUsers() {
        LocalDateTime limit = LocalDateTime.now().minusMonths(3);
        List<UserEntity> disable = userRepository.findByLastLoginBefore(limit);

        if (disable.isEmpty()) {
            return;
        }

        disable.forEach(user -> user.setIsActive(false));
        userRepository.saveAll(disable);

        List<String> usernames = disable.stream().map(UserEntity::getUsername).toList();

        String formattedUsernames = String.join(", ", usernames);
        rabbitTemplate.convertAndSend("auth.events","auth.users.updated", logService.logAction("DISABLE_USERS", formattedUsernames, "Users who have not logged in for over a 3 months have been deactivated."));
    }


    @Scheduled(cron = "0 0 0 * * *")
    public void setExpiredUsers() {
        LocalDateTime limit = LocalDateTime.now().minusMonths(1);
        List<UserEntity> reset = userRepository.findByLastPasswordResetDateBefore(limit);
        if (reset.isEmpty()) {
            return;
        }
        reset.forEach(user -> user.setCredentialsNonExpired(false));
        userRepository.saveAll(reset);
        List<String> usernames = reset.stream().map(UserEntity::getUsername).toList();
        String formattedUsernames = String.join(", ", usernames);
        rabbitTemplate.convertAndSend("auth.events", "auth.events.updated", logService.logAction("SET_CREDENTIALS_EXPIRED", formattedUsernames, "Users who have not redefined your password, was changed to expired for security."));
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void disableAccountsTemporary() {
        LocalDateTime limit = LocalDateTime.now().minusYears(1);
        List<UserEntity> disable = userRepository.findByCreatedAtBeforeAndIsNotTemporary(limit, false);
        if (disable.isEmpty()) {
            return;
        }
        disable.forEach(user -> user.setEnabled(false));
        userRepository.saveAll(disable);
        List<String> usernames = disable.stream().map(UserEntity::getUsername).toList();
        String formattedUsernames = String.join(", ", usernames);
        rabbitTemplate.convertAndSend("auth.events", "auth.users.login.reset-password", logService.logAction("DISABLE_ACCOUNTS_TEMPORARY", formattedUsernames, "Accounts defined temporarily, was changed to disabled."));

    }

    @PostConstruct
    public UserEntity createFirstUserToTests() {
        Optional<UserEntity> user = userRepository.findByUsername("system");
        if (user.isEmpty()) {
            UserEntity entity = new UserEntity();
            entity.setEnabled(true);
            entity.setUsername("system");
            entity.setCreatedAt(LocalDateTime.now());
            entity.setLastPasswordResetDate(LocalDateTime.now());
            entity.setPassword(passwordEncoder.encode("Sit@2024"));
            entity.setCreatedBy("SYSTEM_AUTOMATIZED");
            entity.setEmail("donotreply@sita.aero");
            entity.setUpdatedBy(entity.getCreatedBy());
            entity.setUpdatedAt(entity.getCreatedAt());
            entity.setIsActive(true);
            entity.setCredentialsNonExpired(true);
            entity.setFirstName("System");
            entity.setLastName("Application");
            entity.setIsNotTemporary(false);
            entity.setRole("MASTER");
            entity.setAccountNonExpired(true);
            entity.setAccountNonLocked(true);
            userRepository.save(entity);
            rabbitTemplate.convertAndSend("auth.events", "auth.users.created.system",  logService.logAction("CREATE_FIRST_USER", entity.getUsername(), "User created."));
            return entity;
        }
        return user.get();
    }

    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Object login(LoginRequest request) {

        long expirationTimestamp = System.currentTimeMillis() + EXPIRATION_TIME;

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Not found user"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            rabbitTemplate.convertAndSend("auth.events", "auth.users.login.error", logService.logAction("LOGIN", request.getUsername(), "Wrong password"));
            throw new BadCredentialsException("Password incorrect");
        }

        if (!Boolean.TRUE.equals(user.getCredentialsNonExpired())) {
            String resetToken = jwtService.generatePasswordResetToken(user);
            rabbitTemplate.convertAndSend("auth.events", "auth.users.login.warning", logService.logAction("LOGIN", request.getUsername(), "Is necessary redefine your password"));
            return new GenericResponse(307, "Is necessary redefine your password", resetToken);
        }

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            rabbitTemplate.convertAndSend("auth.events", "auth.users.login.error", logService.logAction("LOGIN", request.getUsername(), "Username with status disabled"));
            throw new BadCredentialsException("User is not enabled");
        }

        String token = jwtService.generateToken(user);

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        rabbitTemplate.convertAndSend("auth.events", "auth.users.login.success", logService.logAction(String.format("LOGIN_%s", user.getUsername()), user.getUsername(), "User successfully logged in"));

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUsername(user.getUsername());
        loginResponse.setEmail(user.getEmail());
        loginResponse.setRole(user.getRole());
        loginResponse.setId(user.getId());
        loginResponse.setPermissions(user.getPermissions().stream().map(UserPermissions::getPermissionName).toList());
        loginResponse.setEncryptedToken(token);
        loginResponse.setExp(expirationTimestamp);
        loginResponse.setName(user.getFirstName() + " " + user.getLastName());
        return loginResponse;
    }


    public LoginResponse register(RegisterRequest request) {
        long expirationTimestamp = System.currentTimeMillis() + EXPIRATION_TIME;

        String password_regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[^a-zA-Z0-9]).{8,}$";
        String email_regex = "^[a-zA-Z0-9._%+-]+@(sita\\.aero|noreply\\.com|tecnocomp\\.com\\.br)$";

        if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
            rabbitTemplate.convertAndSend("auth.events", "auth.users.created.error", logService.logAction(String.format("CREATE_NEW_USER%s", request.getUsername()), request.getUsername(), "Username or email address already in use"));
            throw new IllegalArgumentException("Username or email address already in use");
        }

        if (!Pattern.matches(password_regex, request.getPassword())) {
            throw new IllegalArgumentException("The password must be at least 8 characters long and a special character");
        }
        if (!Pattern.matches(email_regex, request.getEmail())) {
            throw new IllegalArgumentException("The email must be a sita account email address");
        }

        UserEntity user = userMapper.registerToEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        user.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        user.setLastPasswordResetDate(LocalDateTime.now());
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setIsActive(true);

        UserEntity savedUser = userRepository.save(user);

        // ✅ Novo JWT com authorities incluídas
        String token = jwtService.generateToken(savedUser);

        rabbitTemplate.convertAndSend("auth.events", "auth.users.created.success", logService.logAction(String.format("CREATE_NEW_USER_%s", request.getUsername()), SecurityContextHolder.getContext().getAuthentication().getName(), "User successfully created"));
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUsername(user.getUsername());
        loginResponse.setEmail(user.getEmail());
        loginResponse.setRole(user.getRole());
        loginResponse.setId(user.getId());
        loginResponse.setEncryptedToken(token);
        loginResponse.setExp(expirationTimestamp);
        return loginResponse;
    }


    public UserUpdateResponse updateUser(UserUpdateRequest request, Long id) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        userMapper.updateEntityFromDto(request, existingUser, regionRepository, permissionRepository);
        existingUser.setUpdatedBy(getCurrentUsername());
        userRepository.save(existingUser);

        rabbitTemplate.convertAndSend("auth.events", "auth.users.updated.success",
                logService.logAction(
                        String.format("UPDATE_USER_%s", existingUser.getUsername()),
                        SecurityContextHolder.getContext().getAuthentication().getName(),
                        "User successfully updated"
                )
        );

        return new UserUpdateResponse(202, "User was updated");
    }

    public GenericResponse adminResetPassword(String username, AdminResetPasswordRequest request) {
        UserEntity user = getUserByUsername(username);
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setLastPasswordResetDate(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(getCurrentUsername());
        userRepository.save(user);

        rabbitTemplate.convertAndSend("auth.events", "auth.users.updated.reset-password", logService.logAction(String.format("RESET_PASSWORD_USER_%s", username), SecurityContextHolder.getContext().getAuthentication().getName(), "Password reset by Admin"));
        return new GenericResponse(200, "Password was redefined by user Admin", null);
    }

    public GenericResponse sendTokenToResetPassword(ForgotPasswordRequest request) {
        UserEntity user = getUserByUsername(request.getUsername());
        String token = jwtService.generatePasswordResetToken(user);
        String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;

        sendResetPasswordEmail(user.getEmail(), resetLink);

        rabbitTemplate.convertAndSend("auth.events", "auth.users.login.mail", logService.logAction(String.format("SEND_TOKEN_EMAIL_TO_%s", request.getUsername()), request.getUsername(), "Send link with token to reset password"));
        return new GenericResponse(202, "Password reset link was generated", "");
    }

    public List<UserResponse> getUsersByFilters(String role, Boolean isActive) {
        List<UserEntity> users;

        if (role != null && isActive != null) {
            users = userRepository.findByRoleAndIsActive(role, isActive);
        } else if (role != null) {
            users = userRepository.findByRole(role);
        } else if (isActive != null) {
            users = userRepository.findByIsActive(isActive);
        } else {
            users = userRepository.findAll();
        }

        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public GenericResponse softDeleteUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found user by ID: " + id));

        if (!user.getEnabled()) {
            return new GenericResponse(409, "User already disabled", null);
        }

        user.setEnabled(false);
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(getCurrentUsername()); // já tem esse método

        userRepository.save(user);
        rabbitTemplate.convertAndSend("auth.events", "auth.users.updated.delete", logService.logAction(String.format("DELETE_USER_%s", id), SecurityContextHolder.getContext().getAuthentication().getName(), "User was disabled"));

        return new GenericResponse(200, "Status user changed to disabled, successful", null);
    }


    public GenericResponse changeOwnPassword(ChangePasswordRequest request) {
        String password_regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[^a-zA-Z0-9]).8$";
        String username = getCurrentUsername();
        UserEntity user = getUserByUsername(username);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }
        if (!Pattern.matches(password_regex, user.getPassword())) {
            throw new BadCredentialsException("The password must be at least 8 characters long and a special character");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setLastPasswordResetDate(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(username);
        userRepository.save(user);

        rabbitTemplate.convertAndSend("auth.events", "auth.users.updated.reset-password", logService.logAction(String.format("RESET_PASSWORD_USER_%s", username), SecurityContextHolder.getContext().getAuthentication().getName(), "Password was updated by self user"));

        return new GenericResponse(200, "Password was changed", null);
    }

    public GenericResponse resetPassword(String token, UserPasswordRequest request) {
        validateResetToken(token);
        String username = jwtService.extractUsername(token);
        UserEntity user = getUserByUsername(username);

        userMapper.updatePasswordFromRequest(request, user, passwordEncoder);
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy("RESET_PASSWORD");
        user.setCredentialsNonExpired(true);
        userRepository.save(user);

        rabbitTemplate.convertAndSend("auth.events", "auth.users.updated.reset-password", logService.logAction(String.format("RESET_PASSWORD_USER_%s", username), "system_application", "Password was updated by email link"));
        return new GenericResponse(200, "Password was redefined with successful", null);
    }

    public UserResponse getUserById(Long id) {
        Optional<UserEntity> user = userRepository.findById(id);
        return user.stream().map(userMapper::toUserResponse).toList().get(0);
    }

    private void validateResetToken(String token) {
        if (!jwtService.isTokenValid(token) || !jwtService.isResetToken(token)) {
            throw new RuntimeException("Token is invalid or expired");
        }
    }

    private UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Not found user: " + username));
    }

    private void sendResetPasswordEmail(String to, String link) {
        RabbitMQMail mail = new RabbitMQMail();
        mail.setFrom("system@stock.aero");
        mail.setTo(Collections.singletonList(to));
        mail.setSubject("Reset password link");
        mail.setBody("Reset password link, Follow this link: \n" + link);
        mail.setLink(Collections.singletonList(link));

        rabbitTemplate.convertAndSend("auth.events", "auth.users.reset", mail);
    }

    public List<UserResponse> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }
}
