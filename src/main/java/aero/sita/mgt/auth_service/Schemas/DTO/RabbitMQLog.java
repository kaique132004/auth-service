package aero.sita.mgt.auth_service.Schemas.DTO;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class RabbitMQLog {
    private String action;
    private String performedBy;
    private String details;
    private LocalDateTime timestamp;
}
