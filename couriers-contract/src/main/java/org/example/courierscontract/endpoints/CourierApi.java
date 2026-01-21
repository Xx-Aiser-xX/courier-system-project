package org.example.courierscontract.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.courierscontract.dto.request.CreateCourierRequest;
import org.example.courierscontract.dto.request.UpdateCourierLocationRequest;
import org.example.courierscontract.dto.request.UpdateCourierStatusRequest;
import org.example.courierscontract.dto.response.CourierResponse;
import org.example.courierscontract.dto.response.StatusResponse;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.PagedModel;


import java.util.UUID;

@Tag(name = "API курьеров", description = "взаимодействие с курьерами")
@RequestMapping("/api/couriers")
public interface CourierApi {
    @Operation(summary = "Получение публичной информации о курьере")
    @GetMapping("/{id}")
    EntityModel<CourierResponse> getCourierById(@PathVariable UUID id);

    @Operation(summary = "обновление местоположения курьера")
    @PutMapping("/{id}/location")
    EntityModel<CourierResponse> updateCourierLocation(@PathVariable UUID id, @Valid @RequestBody UpdateCourierLocationRequest request);

    @Operation(summary = "обновление статуса курьера")
    @PutMapping("/status")
    EntityModel<CourierResponse> updateCourierStatus(@Valid @RequestBody UpdateCourierStatusRequest request);

    @Operation(summary = "Принять предложенный заказ")
    @PostMapping("/orders/{orderId}/accept")
    ResponseEntity<Void> acceptOrder(@PathVariable UUID orderId);

    @Operation(summary = "Отказаться от предложенного заказа")
    @PostMapping("/orders/{orderId}/decline")
    ResponseEntity<Void> declineOrder(@PathVariable UUID orderId);

    @Operation(summary = "Изменить статус доставки")
    @PostMapping("/orders/{orderId}/change-status")
    ResponseEntity<Void> changeDeliveryStatus(@PathVariable UUID orderId, @RequestParam String newStatus);
}