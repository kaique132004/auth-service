package aero.sita.mgt.auth_service.Services;

import aero.sita.mgt.auth_service.Schemas.AppMapper;
import aero.sita.mgt.auth_service.Schemas.DTO.*;
import aero.sita.mgt.auth_service.Schemas.Entitys.RegionEntity;
import aero.sita.mgt.auth_service.Schemas.Entitys.RegionRepository;
import aero.sita.mgt.auth_service.Schemas.Entitys.UserPermissionRepository;
import aero.sita.mgt.auth_service.Schemas.Entitys.UserPermissions;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final JwtService jwtService;

    private final RabbitTemplate rabbitTemplate;

    private final AppMapper appMapper;

    private final UserPermissionRepository userPermissionRepository;

    private final RegionRepository regionRepository;


    public PermissionRequest createPermission(PermissionRequest permissionRequest) {
        if (userPermissionRepository.findUserPermissionsByPermissionName(permissionRequest.getPermissionName()).isPresent()) {
            throw new IllegalArgumentException("Already have permission: " + permissionRequest.getPermissionName());
        }

        UserPermissions permissions = appMapper.createUserPermissions(permissionRequest);
        permissions.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        permissions.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        userPermissionRepository.save(permissions);

        return permissionRequest;
    }

    public PermissionResponse getPermissionByName(String name) {
        UserPermissions permission = userPermissionRepository.findUserPermissionsByPermissionName(name)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + name));
        return appMapper.toPermissionResDto(permission);
    }


    public List<PermissionResponse> getAllPermissions() {
        return userPermissionRepository.findAll().stream()
                .map(appMapper::toPermissionResDto)
                .collect(Collectors.toList());
    }

    public PermissionRequest updatePermission(String name, PermissionRequest request) {
        UserPermissions permission = userPermissionRepository.findUserPermissionsByPermissionName(name)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + name));

        appMapper.updatePermissionFromDto(request, permission);
        permission.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        userPermissionRepository.save(permission);

        return appMapper.toPermissionDto(permission);
    }

    public GenericResponse deletePermission(String permissionName) {
        UserPermissions permission = userPermissionRepository.findUserPermissionsByPermissionName(permissionName)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionName));

        userPermissionRepository.delete(permission);

        return new GenericResponse(200, "Permission deleted successfully", null);
    }


    public RegionRequest createRegion(RegionRequest regionRequest) {
        if (regionRepository.existsRegionEntityByRegionCode(regionRequest.getRegionCode())) {
            throw new IllegalArgumentException("Already have region: " + regionRequest.getRegionCode());
        }

        RegionEntity region = appMapper.createRegion(regionRequest);
        region.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        region.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        regionRepository.save(region);

        return regionRequest;
    }

    public List<RegionResponse> getAllRegions() {
        return regionRepository.findAll().stream()
                .map(appMapper::toRegionResponse)
                .collect(Collectors.toList());
    }

    public RegionRequest updateRegion(String code, RegionRequest request) {
        RegionEntity existingRegion = regionRepository.findByRegionCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Region not found: " + code));

        appMapper.updateRegionFromDto(request, existingRegion);
        existingRegion.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        regionRepository.save(existingRegion);

        return appMapper.toRegionDto(existingRegion);
    }

    public GenericResponse deleteRegion(String code) {
        RegionEntity region = regionRepository.findByRegionCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Region not found: " + code));

        regionRepository.delete(region);

        return new GenericResponse(200, "Region deleted successfully", null);
    }

    public RegionRequest getRegionByCode(String code) {
        RegionEntity region = regionRepository.findByRegionCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Region not found: " + code));
        return appMapper.toRegionDto(region);
    }

}
