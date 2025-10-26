package org.example.courierscontract.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.courierscontract.dto.request.CreateUserRequest;
import org.example.courierscontract.dto.response.StatusResponse;
import org.example.courierscontract.dto.response.UserResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "API Пользователей", description = "Взаимодействие с пользователями")
@RequestMapping("/api/users")
public interface UserApi {

    @Operation(summary = "Создание нового пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или пользователь с таким email/телефоном уже существует",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request);

    @Operation(summary = "Получение пользователя по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    })
    @GetMapping("/{id}")
    EntityModel<UserResponse> getUserById(@PathVariable UUID id);
}
