package aero.sita.mgt.auth_service.Schemas.DTO;

import lombok.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LoginResponse {
    private String encryptedToken;
    private String username;
    private String role;
    private long exp;
    private List<String> permissions;
    private Long id;
    private String email;
    private String name;
    private Map<String, SectionSettings> siteSettings;
}