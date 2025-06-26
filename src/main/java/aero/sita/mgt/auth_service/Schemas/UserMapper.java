package aero.sita.mgt.auth_service.Schemas;

import aero.sita.mgt.auth_service.Schemas.DTO.*;
import aero.sita.mgt.auth_service.Schemas.Entitys.*;
import org.mapstruct.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "siteSettings", ignore = true)
    @Mapping(target = "isNotTemporary", constant = "true")
    UserEntity registerToEntity(RegisterRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "regions", expression = "java(resolveRegions(request.getRegionCodes(), regionRepository))")
    @Mapping(target = "permissions", expression = "java(resolvePermissions(request.getPermissions(), permissionRepository))")
    void updateEntityFromDto(UserUpdateRequest request,
                             @MappingTarget UserEntity entity,
                             @Context RegionRepository regionRepository,
                             @Context UserPermissionRepository permissionRepository);

    @Mapping(target = "password", expression = "java(encoder.encode(request.getNewPassword()))")
    void updatePasswordFromRequest(UserPasswordRequest request, @MappingTarget UserEntity entity, @Context PasswordEncoder encoder);

    @Mapping(target = "regions", source = "regions")
    @Mapping(target = "permissions", source = "permissions")
    UserResponse toUserResponse(UserEntity entity);

    List<RegionRequest> toRegionDtoList(Set<RegionEntity> entities);

    // Métodos auxiliares para resolução de entidades a partir dos DTOs
    default Set<RegionEntity> resolveRegions(Set<RegionRequest> regionRequests, RegionRepository repository) {
        if (regionRequests == null) return null;
        return regionRequests.stream()
                .map(req -> repository.findByRegionCode(req.getRegionCode())
                        .orElseThrow(() -> new RuntimeException("Região não encontrada: " + req.getRegionCode())))
                .collect(Collectors.toSet());
    }

    default Set<UserPermissions> resolvePermissions(Set<PermissionRequest> permissionRequests, UserPermissionRepository repository) {
        if (permissionRequests == null) return null;
        return permissionRequests.stream()
                .map(req -> repository.findByPermissionName(req.getPermissionName())
                        .orElseThrow(() -> new RuntimeException("Permissão não encontrada: " + req.getPermissionName())))
                .collect(Collectors.toSet());
    }
}
