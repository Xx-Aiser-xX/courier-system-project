package org.example.couriers.graphql;

import com.netflix.graphql.dgs.*;
import graphql.schema.DataFetchingEnvironment;
import org.example.couriers.service.CourierService;
import org.example.couriers.service.OrderService;
import org.example.couriers.service.SecurityService;
import org.example.couriers.service.UserService;
import org.example.courierscontract.dto.request.CreateOrderRequest;
import org.example.courierscontract.dto.response.CourierResponse;
import org.example.courierscontract.dto.response.OrderResponse;
import org.example.courierscontract.dto.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@DgsComponent
public class OrderDataFetcher {

    private final OrderService orderService;
    private final UserService userService;
    private final CourierService courierService;
    private final SecurityService securityService;


    @Autowired
    public OrderDataFetcher(OrderService orderService, UserService userService, CourierService courierService, SecurityService securityService) {
        this.orderService = orderService;
        this.userService = userService;
        this.courierService = courierService;
        this.securityService = securityService;
    }

    @DgsQuery
    public OrderResponse orderById(@InputArgument String id) {
        return orderService.getOrderById(UUID.fromString(id));
    }

    @DgsMutation
    public OrderResponse createOrder(@InputArgument("input") CreateOrderRequest request) {
        UUID userId = securityService.getCurrentUserId();
        return orderService.createOrder(userId, request);
    }

    @DgsData(parentType = "Order", field = "user")
    public UserResponse user(DataFetchingEnvironment dfe) {
        OrderResponse order = dfe.getSource();
        return userService.getUserById(order.getUserId());
    }

    @DgsData(parentType = "Order", field = "courier")
    public CourierResponse courier(DataFetchingEnvironment dfe) {
        OrderResponse order = dfe.getSource();
        if (order.getCourierId() == null) {
            return null;
        }
        return courierService.getCourierById(order.getCourierId());
    }
}