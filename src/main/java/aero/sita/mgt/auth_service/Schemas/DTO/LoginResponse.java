package aero.sita.mgt.auth_service.Schemas.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String encryptedToken;
    private String username;
    private String role;
}