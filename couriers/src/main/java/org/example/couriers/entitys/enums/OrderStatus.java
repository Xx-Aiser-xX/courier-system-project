package org.example.couriers.entitys.enums;

public enum OrderStatus {
    CREATED(0),
    PROCESSING(1),
    IN_TRANSIT(2),
    DELIVERED(3),
    CANCELLED(4);

    private final int value;

    OrderStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
