package aero.sita.mgt.auth_service.Services;

import aero.sita.mgt.auth_service.Schemas.DTO.RabbitMQLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class LogService {

    private final RabbitTemplate rabbitTemplate;

    public LogService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public RabbitMQLog logAction(String action, String performedBy, String details) {
        RabbitMQLog log = new RabbitMQLog();
        log.setAction(action);
        log.setPerformedBy(performedBy);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        return log;
    }

    private void safeSend(String exchange, String routingKey, Object message) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
        } catch (AmqpException e) {
            log.error("RabbitMQ send failed: {}", e.getMessage());
        }
    }

}
