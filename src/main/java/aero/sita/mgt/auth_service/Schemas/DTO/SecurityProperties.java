package aero.sita.mgt.auth_service.Schemas.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "security")
@Getter
@Setter
public class SecurityProperties {
    private List<String> publicEndpoints;

    /*
     *  Mapper endpoints -> roles list
     */

    private Map<String, List<String>> protectedEndpoints;
}
