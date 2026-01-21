package edu.rutmiit.demo.events;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID userId,
        String senderAddress,
        String recipientAddress,
        BigDecimal price
) {}