package org.example.couriers.entitys.enums;

public enum DeliveryType {
    STANDARD(0),
    EXPRESS(1),
    SCHEDULED(2);

    private final int value;

    DeliveryType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
