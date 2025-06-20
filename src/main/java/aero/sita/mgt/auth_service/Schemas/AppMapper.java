package aero.sita.mgt.auth_service.Schemas;

import aero.sita.mgt.auth_service.Schemas.DTO.PermissionRequest;
import aero.sita.mgt.auth_service.Schemas.DTO.RegionRequest;
import aero.sita.mgt.auth_service.Schemas.Entitys.RegionEntity;
import aero.sita.mgt.auth_service.Schemas.Entitys.UserPermissions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AppMapper {

    // Criação de permissões
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "isActive", constant = "true")
    UserPermissions createUserPermissions(PermissionRequest request);

    // Criação de regiões
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "isActive", constant = "true")
    RegionEntity createRegion(RegionRequest request);

    // Converters
    RegionRequest toRegionDto(RegionEntity entity);

    PermissionRequest toPermissionDto(UserPermissions entity);

    // Atualização de entidades existentes
    void updateRegionFromDto(RegionRequest dto, @MappingTarget RegionEntity entity);

    void updatePermissionFromDto(PermissionRequest dto, @MappingTarget UserPermissions entity);
}