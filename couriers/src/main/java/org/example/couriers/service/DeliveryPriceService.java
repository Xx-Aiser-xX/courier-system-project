package org.example.couriers.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryPriceService {
    BigDecimal calculateDeliveryPrice(String from, String to, double weight, UUID userId);
}

