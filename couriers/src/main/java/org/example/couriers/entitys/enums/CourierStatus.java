package org.example.couriers.entitys.enums;

public enum CourierStatus {
    FREE(0),
    BUSY(1),
    INACTIVE(2);

    private final int value;

    CourierStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
