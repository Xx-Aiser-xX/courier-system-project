package org.example.couriers.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.example.couriers.service.CourierService;
import org.example.courierscontract.dto.request.UpdateCourierLocationRequest;
import org.example.courierscontract.dto.request.UpdateCourierStatusRequest;
import org.example.courierscontract.dto.response.CourierResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.UUID;

@DgsComponent
public class CourierDataFetcher {

    private final CourierService courierService;

    @Autowired
    public CourierDataFetcher(CourierService courierService) {
        this.courierService = courierService;
    }

    @DgsQuery
    public CourierResponse courierById(@InputArgument String id) {
        return courierService.getCourierById(UUID.fromString(id));
    }

    @DgsQuery
    public Page<CourierResponse> allCouriers(@InputArgument int page, @InputArgument int size) {
        return courierService.getAllCouriers(page, size);
    }

    @DgsMutation
    public CourierResponse updateCourierStatus(@InputArgument String id, @InputArgument("input") Map<String, String> input) {
        UpdateCourierStatusRequest request = new UpdateCourierStatusRequest(input.get("status"));
        return courierService.updateCourierStatus(UUID.fromString(id), request);
    }

    @DgsMutation
    public CourierResponse updateCourierLocation(@InputArgument String id, @InputArgument("input") Map<String, Double> input) {
        UpdateCourierLocationRequest request = new UpdateCourierLocationRequest(input.get("latitude"), input.get("longitude"));
        return courierService.updateCourierLocation(UUID.fromString(id), request);
    }
}