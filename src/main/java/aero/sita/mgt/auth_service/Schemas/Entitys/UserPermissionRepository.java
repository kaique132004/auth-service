package aero.sita.mgt.auth_service.Schemas.Entitys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermissions, String> {
    Optional<UserPermissions> findUserPermissionsByPermissionName(String permissionName);

    Optional<UserPermissions> findByPermissionName(String permissionName);
}
