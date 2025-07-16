package aero.sita.mgt.auth_service.Schemas.DTO;

import aero.sita.mgt.auth_service.Schemas.Entitys.UserEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class UserUpdateRequest {

    private String firstName;

    private String lastName;

    private String role;

    private Boolean isActive;

    private String updatedBy;

    private LocalDateTime updatedAt;

    private LocalDateTime lastLogin;

    private LocalDateTime lastPasswordResetDate;

    private Boolean isNotTemporary;

    private Set<RegionRequest> regionCodes;

    private Set<PermissionRequest> permissions;

    private Map<String, SectionSettings> siteSettings;

    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private Boolean enabled;
}
