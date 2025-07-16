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
        @Mapping(target = "isNotTemporary", constant = "true")
        UserEntity registerToEntity(RegisterRequest request);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "password", ignore = true)
        @Mapping(target = "email", ignore = true)
        @Mapping(target = "username", ignore = true)
        @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
        @Mapping(target = "authorities", ignore = true)
        @Mapping(target = "siteSettings", source = "siteSettings")  // Usando método padrão
        void updateEntityFromDtoBase(UserUpdateRequest request, @MappingTarget UserEntity entity);

        @Mapping(target = "siteSettings", source = "siteSettings")  // Usando método padrão
        default void updateEntityFromDto(UserUpdateRequest request,
                                         @MappingTarget UserEntity entity,
                                         @Context RegionRepository regionRepository,
                                         @Context UserPermissionRepository permissionRepository) {
                updateEntityFromDtoBase(request, entity);
                entity.setRegions(resolveRegions(request.getRegionCodes(), regionRepository));
                entity.setPermissions(resolvePermissions(request.getPermissions(), permissionRepository));
        }

        @Mapping(target = "password", expression = "java(encoder.encode(request.getNewPassword()))")
        void updatePasswordFromRequest(UserPasswordRequest request,
                                       @MappingTarget UserEntity entity,
                                       @Context PasswordEncoder encoder);

        @Mapping(target = "regions", source = "regions")
        @Mapping(target = "permissions", source = "permissions")
        @Mapping(target = "siteSettings", source = "siteSettings")
        UserResponse toUserResponse(UserEntity entity);

        List<RegionRequest> toRegionDtoList(Set<RegionEntity> entities);

        default Set<RegionEntity> resolveRegions(Set<RegionRequest> regionRequests, RegionRepository repository) {
                if (regionRequests == null)
                        return null;
                return regionRequests.stream()
                        .map(req -> repository.findByRegionCode(req.getRegionCode())
                                .orElseThrow(() -> new RuntimeException(
                                        "Região não encontrada: " + req.getRegionCode())))
                        .collect(Collectors.toSet());
        }

        default Set<UserPermissions> resolvePermissions(Set<PermissionRequest> permissionRequests,
                                                        UserPermissionRepository repository) {
                if (permissionRequests == null)
                        return null;
                return permissionRequests.stream()
                        .map(req -> repository.findByPermissionName(req.getPermissionName())
                                .orElseThrow(() -> new RuntimeException("Permissão não encontrada: "
                                        + req.getPermissionName())))
                        .collect(Collectors.toSet());
        }

        // Método qualificado que retorna o map
        default Map<String, SectionSettings> mapSiteSettings(SiteSettingsDTO dto) {
                if (dto == null || dto.getSections() == null) return null;

                Map<String, SectionSettings> settings = new HashMap<>();

                for (Map.Entry<String, SiteSettingsDTO.SectionDTO> entry : dto.getSections().entrySet()) {
                        String sectionName = entry.getKey();
                        SiteSettingsDTO.SectionDTO sectionData = entry.getValue();

                        // Convertendo as colunas, se existirem
                        List<String> columns = sectionData.getColumns() != null ? sectionData.getColumns() : Collections.emptyList();

                        // Adicionando a seção no mapa de settings
                        settings.put(sectionName, new SectionSettings(columns));
                }

                return settings;
        }

        default List<String> splitColumns(String columnsString) {
                if (columnsString == null || columnsString.isBlank()) return Collections.emptyList();
                return Arrays.stream(columnsString.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
        }
}
