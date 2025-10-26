package org.example.courierscontract.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.courierscontract.dto.request.UpdateCourierLocationRequest;
import org.example.courierscontract.dto.request.UpdateCourierStatusRequest;
import org.example.courierscontract.dto.response.CourierResponse;
import org.example.courierscontract.dto.response.StatusResponse;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.PagedModel;


import java.util.UUID;

@Tag(name = "API Курьеров", description = "Взаимодействие с курьерами")
@RequestMapping("/api/couriers")
public interface CourierApi {

    @Operation(summary = "Получение списка всех курьеров")
    @ApiResponse(responseCode = "200", description = "Список курьеров")
    @GetMapping
    PagedModel<EntityModel<CourierResponse>> getAllCouriers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            PagedResourcesAssembler<CourierResponse> assembler);

    @Operation(summary = "Получение курьера по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Курьер найден"),
            @ApiResponse(responseCode = "404", description = "Курьер не найден",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class)))})
    @GetMapping("/{id}")
    EntityModel<CourierResponse> getCourierById(@PathVariable UUID id);

    @Operation(summary = "Обновление местоположения курьера")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Местоположение обновлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные координаты"),
            @ApiResponse(responseCode = "404", description = "Курьер не найден")
    })
    @PutMapping("/{id}/location")
    EntityModel<CourierResponse> updateCourierLocation(@PathVariable UUID id, @Valid @RequestBody UpdateCourierLocationRequest request);

    @Operation(summary = "Обновление статуса курьера")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус обновлен"),
            @ApiResponse(responseCode = "400", description = "Некорректный статус"),
            @ApiResponse(responseCode = "404", description = "Курьер не найден")})
    @PutMapping("/{id}/status")
    EntityModel<CourierResponse> updateCourierStatus(@PathVariable UUID id, @Valid @RequestBody UpdateCourierStatusRequest request);
}