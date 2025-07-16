package aero.sita.mgt.auth_service.Schemas.DTO;

import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SiteSettingsDTO {

    private Map<String, SectionDTO> sections = new HashMap<>();

    @Data
    public static class SectionDTO {
        private List<String> columns;  // Somente colunas agora
    }
}
