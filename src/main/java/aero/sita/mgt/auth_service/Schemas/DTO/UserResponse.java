package aero.sita.mgt.auth_service.Schemas.DTO;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private Boolean isActive;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime lastPasswordResetDate;
    private Boolean isNotTemporary;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private List<RegionRequest> regions;
    private List<PermissionRequest> permissions;
    private Map<String, String> siteSettings;
}