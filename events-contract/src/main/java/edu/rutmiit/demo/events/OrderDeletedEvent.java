package edu.rutmiit.demo.events;

import java.io.Serializable;
import java.util.UUID;

public record OrderDeletedEvent(
        UUID orderId
) implements Serializable {
}
