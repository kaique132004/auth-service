package aero.sita.mgt.auth_service.Schemas.Entitys;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<UserEntity> findByLastLoginBefore(LocalDateTime limit);

    List<UserEntity> findByLastPasswordResetDateBefore(LocalDateTime limit);

    List<UserEntity> findByCreatedAtBeforeAndIsNotTemporary(LocalDateTime limit, Boolean aBoolean);

    List<UserEntity> findByRole(String role);

    List<UserEntity> findByIsActive(Boolean isActive);

    List<UserEntity> findByRoleAndIsActive(String role, Boolean isActive);
}
