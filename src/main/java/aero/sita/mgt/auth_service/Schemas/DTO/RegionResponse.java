package aero.sita.mgt.auth_service.Schemas.DTO;

import lombok.Data;

@Data
public class RegionResponse {
    private String id;
    private String regionCode;
    private String regionName;
    private String cityName;
    private String countryName;
    private String stateName;
    private String addressCode;
    private String responsibleName;
    private Boolean isActive;
    private Boolean containsAgentsLocal;
}
