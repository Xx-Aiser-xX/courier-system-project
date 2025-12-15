package edu.rutmiit.demo.events;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID userId,
        String senderAddress,
        String recipientAddress,
        BigDecimal price
) implements Serializable {
}