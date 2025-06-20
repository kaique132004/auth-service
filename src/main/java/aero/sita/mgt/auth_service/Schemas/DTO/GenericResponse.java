package aero.sita.mgt.auth_service.Schemas.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse {
    private Integer status;
    private String message;
    private String token;
}
