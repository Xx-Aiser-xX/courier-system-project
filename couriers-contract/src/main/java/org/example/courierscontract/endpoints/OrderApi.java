package org.example.courierscontract.endpoints;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.courierscontract.dto.request.CreateOrderRequest;
import org.example.courierscontract.dto.response.OrderResponse;
import org.example.courierscontract.dto.response.StatusResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@Tag(name = "API заказов", description = "взаимодействие с заказами")
@RequestMapping("/api/orders")
public interface OrderApi {

    @Operation(summary = "создание нового заказа")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request);

    @Operation(summary = "получение заказа по ID")
    @GetMapping("/{id}")
    EntityModel<OrderResponse> getOrderById(@PathVariable UUID id);

    @Operation(summary = "удаление заказа по ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<Void> deleteOrder(@PathVariable UUID id);

    @GetMapping("/calculate")
    @Operation(summary = "получение стоимости заказа")
    BigDecimal checkPrice(@RequestParam String from, @RequestParam String to,
            @RequestParam double weight, @RequestParam(required = false) UUID userId);
}
