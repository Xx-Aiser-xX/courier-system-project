package edu.rutmiit.demo.events;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record PriceCalculatedEvent(
        UUID userId,
        BigDecimal price,
        String currency,
        String fromAddress,
        String toAddress
) implements Serializable {
}