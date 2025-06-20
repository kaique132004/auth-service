package aero.sita.mgt.auth_service.Schemas.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "region_table")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment no MySQL
    private Long id;

    @Column(name = "region_code", nullable = false, unique = true, length = 50)
    private String regionCode;

    @Column(name = "region_name", nullable = false, length = 100)
    private String regionName;

    @Column(name = "city_name", length = 100)
    private String cityName;

    @Column(name = "country_name", length = 100)
    private String countryName;

    @Column(name = "state_name", length = 100)
    private String stateName;

    @Column(name = "address_code", length = 50)
    private String addressCode;

    @Column(name = "responsible_name", length = 150)
    private String responsibleName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "is_active")
    private Boolean isActive;
}
