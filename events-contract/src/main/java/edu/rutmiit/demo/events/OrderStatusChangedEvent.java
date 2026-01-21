package edu.rutmiit.demo.events;

import java.io.Serializable;
import java.util.UUID;

public record OrderStatusChangedEvent(
        UUID orderId,
        UUID userId,
        String newStatus,
        String message
){}