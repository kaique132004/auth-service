package aero.sita.mgt.auth_service.Services;

import aero.sita.mgt.auth_service.Schemas.DTO.RabbitMQLog;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {

    public RabbitMQLog logAction(String action, String performedBy, String details) {
        RabbitMQLog log = new RabbitMQLog();
        log.setAction(action);
        log.setPerformedBy(performedBy);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        return log;
    }
}
