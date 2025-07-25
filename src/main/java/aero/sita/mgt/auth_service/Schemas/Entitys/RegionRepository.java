package aero.sita.mgt.auth_service.Schemas.Entitys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<RegionEntity, String> {
    boolean existsRegionEntityByRegionCode(String s);

    Optional<RegionEntity> findByRegionCode(String code);

    List<RegionEntity> findAll();
}
