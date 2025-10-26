package org.example.couriers.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.example.couriers.service.UserService;
import org.example.courierscontract.dto.request.CreateUserRequest;
import org.example.courierscontract.dto.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.UUID;

@DgsComponent
public class UserDataFetcher {

    private final UserService userService;

    @Autowired
    public UserDataFetcher(UserService userService) {
        this.userService = userService;
    }

    @DgsQuery
    public UserResponse userById(@InputArgument String id) {
        return userService.getUserById(UUID.fromString(id));
    }

    @DgsMutation
    public UserResponse createUser(@InputArgument("input") Map<String, String> input) {
        CreateUserRequest request = new CreateUserRequest(
                input.get("email"),
                input.get("phone"),
                input.get("name")
        );
        return userService.createUser(request);
    }
}