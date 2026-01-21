package org.example.courierscontract.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "root API", description = "точка входа в API")
@RequestMapping("/api")
public interface RootApi {

    @Operation(summary = "получение списка доступных ресурсов")
    @GetMapping
    RepresentationModel<?> getRoot();
}
