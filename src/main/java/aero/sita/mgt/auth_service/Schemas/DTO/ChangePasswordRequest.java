package aero.sita.mgt.auth_service.Schemas.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
}
