package aero.sita.mgt.auth_service.Schemas;


import aero.sita.mgt.auth_service.Schemas.DTO.*;
import aero.sita.mgt.auth_service.Schemas.Entitys.RegionEntity;
import aero.sita.mgt.auth_service.Schemas.Entitys.UserEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
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
    void updateEntityFromDto(UserUpdateRequest request, @MappingTarget UserEntity entity);


    @Mapping(target = "password", expression = "java(encoder.encode(request.getNewPassword()))")
    void updatePasswordFromRequest(UserPasswordRequest request, @MappingTarget UserEntity entity, @Context PasswordEncoder encoder);

    @Mapping(target = "regions", source = "regions")
    @Mapping(target = "permissions", source = "permissions")
    UserResponse toUserResponse(UserEntity entity);

    List<RegionRequest> toRegionDtoList(Set<RegionEntity> entities);
}
