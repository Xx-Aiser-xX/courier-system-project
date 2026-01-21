package org.example.couriers.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.example.couriers.service.SecurityService;
import org.example.couriers.service.UserService;
import org.example.courierscontract.dto.request.CreateUserRequest;
import org.example.courierscontract.dto.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.UUID;

@DgsComponent
public class UserDataFetcher {

    private final UserService userService;
    private final SecurityService securityService;

    @Autowired
    public UserDataFetcher(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @DgsQuery
    public UserResponse userById(@InputArgument String id) {
        return userService.getUserById(UUID.fromString(id));
    }

}