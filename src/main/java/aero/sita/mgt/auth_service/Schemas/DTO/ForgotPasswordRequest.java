package aero.sita.mgt.auth_service.Schemas.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ForgotPasswordRequest {

    @NotBlank
    private String username;
}