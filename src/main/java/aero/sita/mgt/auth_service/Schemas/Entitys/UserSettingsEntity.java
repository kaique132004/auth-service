package aero.sita.mgt.auth_service.Schemas.Entitys;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Id;

@Entity
public class UserSettingsEntity {
    
    @Id
    private String id;

    @Lob
    private String siteSettingsJson;
}
