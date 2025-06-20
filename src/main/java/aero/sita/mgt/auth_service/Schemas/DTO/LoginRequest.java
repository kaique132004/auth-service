package aero.sita.mgt.auth_service.Schemas.DTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
