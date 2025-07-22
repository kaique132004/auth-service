package aero.sita.mgt.auth_service.Schemas.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String role;
    private boolean active;
}
