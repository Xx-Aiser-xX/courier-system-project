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

@Tag(name = "API пользователей", description = "взаимодействие с пользователями")
@RequestMapping("/api/users")
public interface UserApi {

    @Operation(summary = "получение пользователя по ID")
    @GetMapping("/{id}")
    EntityModel<UserResponse> getUserById(@PathVariable UUID id);
}
