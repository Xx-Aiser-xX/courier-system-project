package org.example.courierscontract.endpoints;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public interface RootApi {
    RepresentationModel<?> getRoot();
}
