package aero.sita.mgt.auth_service.Schemas.DTO;

import lombok.Data;

@Data
public class UserPasswordRequest {
    private String token;

    private String username;

    private String newPassword;

    @Override
    public String toString() {
        return "UserPasswordRequest(token=****, username=" + username + ", newPassword=****)";
    }

}
