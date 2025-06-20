package aero.sita.mgt.auth_service.Controllers;

import aero.sita.mgt.auth_service.Schemas.DTO.*;
import aero.sita.mgt.auth_service.Services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Authentication Controller", description = "Controller defined for the access control and entity registration part")
@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login user by username and password", description = "Login of User")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "307", content = @Content(schema = @Schema(implementation = GenericResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema()))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Object response = authService.login(request);

        if (response instanceof LoginResponse) {
            return ResponseEntity.ok(response);
        } else if (response instanceof GenericResponse genericResponse && genericResponse.getStatus() == 307) {
            return ResponseEntity.status(307).body(genericResponse);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse(500, "Error unexpectedly occurred", null));
    }

    @Operation(summary = "Register a new user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request (e.g., user already exists)", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(summary = "Update an existing user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @Transactional
    @PutMapping("/update/{id}")
    public ResponseEntity<UserUpdateResponse> update(@PathVariable String id, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(authService.updateUser(request, id));
    }

    @Operation(summary = "Reset password using token", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    @PostMapping("/reset-password/{token}")
    public ResponseEntity<GenericResponse> resetPassword(@PathVariable String token, @RequestBody UserPasswordRequest request) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(authService.resetPassword(token, request));
    }

    @Operation(summary = "Send password reset token to email")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Token sent successfully", content = @Content(schema = @Schema(implementation = GenericResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<GenericResponse> sendResetPasswordToken(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(authService.sendTokenToResetPassword(request));
    }

    @Operation(summary = "Change password of current authenticated user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Current password is incorrect")
    })
    @PutMapping("/change-password")
    public ResponseEntity<GenericResponse> changePassword(@RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changeOwnPassword(request));
    }

    @Operation(summary = "Admin reset password of a user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset by admin successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/admin-reset-password/{username}")
    public ResponseEntity<GenericResponse> resetPasswordAsAdmin(@PathVariable String username, @RequestBody AdminResetPasswordRequest request) {
        return ResponseEntity.ok(authService.adminResetPassword(username, request));
    }

    @Operation(summary = "Get list of users by role or status (active/inactive)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users list retrieved successfully", content = @Content(schema = @Schema(implementation = UserResponse.class)))
    })
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isActive) {
        List<UserResponse> users = authService.getUsersByFilters(role, isActive);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Soft delete user (disable account)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User disabled successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/users/{id}")
    private ResponseEntity<GenericResponse> deleteUser(@PathVariable String id) {
        GenericResponse response = authService.softDeleteUser(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}