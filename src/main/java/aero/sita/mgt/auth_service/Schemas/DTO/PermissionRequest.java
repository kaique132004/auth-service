package aero.sita.mgt.auth_service.Schemas.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PermissionRequest {

    private String permissionName;

    private String description;

    private Boolean isActive;
}
