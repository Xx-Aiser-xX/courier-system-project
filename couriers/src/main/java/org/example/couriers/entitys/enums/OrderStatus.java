package org.example.couriers.entitys.enums;

public enum OrderStatus {
    CREATED(0),
    SEARCHING(1),
    ASSIGNED(2),
    IN_TRANSIT(3),
    DELIVERED(4),
    CANCELLED(5);

    private final int value;

    OrderStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
