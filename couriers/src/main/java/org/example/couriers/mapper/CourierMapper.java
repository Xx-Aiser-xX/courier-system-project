package org.example.couriers.mapper;

import org.example.couriers.entitys.Courier;
import org.example.couriers.entitys.enums.CourierStatus;
import org.example.courierscontract.dto.request.CreateCourierRequest;
import org.example.courierscontract.dto.request.UpdateCourierLocationRequest;
import org.example.courierscontract.dto.request.UpdateCourierStatusRequest;
import org.example.courierscontract.dto.response.CourierResponse;
import org.example.courierscontract.exception.IncorrectDataException;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourierMapper {

    CourierResponse toResponse(Courier courier);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "assignedOrders", ignore = true)
    Courier toEntity(CreateCourierRequest request);

    void updateLocation(UpdateCourierLocationRequest request, @MappingTarget Courier courier);

    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    void updateStatus(UpdateCourierStatusRequest request, @MappingTarget Courier courier);

    @Named("mapStatus")
    default CourierStatus mapStatus(String status) {
        try {
            return CourierStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IncorrectDataException("неверный статус " + status);
        }
    }


}