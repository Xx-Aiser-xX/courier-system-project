package org.example.couriers.mapper;

import org.example.couriers.entitys.User;
import org.example.courierscontract.dto.request.CreateUserRequest;
import org.example.courierscontract.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    User toEntity(CreateUserRequest request);
}