package aero.sita.mgt.auth_service.Schemas.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserUpdateResponse {

    private Integer statusCode;

    private String statusMessage;
}
