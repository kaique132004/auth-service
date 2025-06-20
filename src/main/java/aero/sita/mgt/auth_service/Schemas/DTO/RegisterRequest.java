package aero.sita.mgt.auth_service.Schemas.DTO;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String role;
}
