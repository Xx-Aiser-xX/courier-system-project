package edu.rutmiit.demo.events;

import java.io.Serializable;
import java.util.UUID;

public record NotificationEvent(
        UUID targetUserId,
        String type,
        String message,
        UUID orderId
) {}