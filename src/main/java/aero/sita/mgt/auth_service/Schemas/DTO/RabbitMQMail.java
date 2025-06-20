package aero.sita.mgt.auth_service.Schemas.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RabbitMQMail {
    private String from;
    private List<String> to;
    private List<String> cc;

    private List<String> bcc;

    private String subject;

    private String body;

    private List<String> link;
}
