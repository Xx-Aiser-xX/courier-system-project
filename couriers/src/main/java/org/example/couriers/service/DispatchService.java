package org.example.couriers.service;

import java.util.*;

public interface DispatchService {
    void startDispatching(UUID orderId);

    void acceptOrder(UUID courierId, UUID orderId);

    void declineOrder(UUID courierId, UUID orderId);
}