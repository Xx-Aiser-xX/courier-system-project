package org.example.courierscontract.dto.response;

import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class OrderResponse extends RepresentationModel<OrderResponse> {
    private final UUID id;
    private final UUID userId;
    private final UUID courierId;
    private final String senderAddress;
    private final String recipientAddress;
    private final String status;
    private final BigDecimal price;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public OrderResponse(UUID id, UUID userId, UUID courierId, String senderAddress, String recipientAddress, String status, BigDecimal price, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.courierId = courierId;
        this.senderAddress = senderAddress;
        this.recipientAddress = recipientAddress;
        this.status = status;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }
    public UUID getUserId() {
        return userId;
    }
    public UUID getCourierId() {
        return courierId;
    }
    public String getSenderAddress() {
        return senderAddress;
    }
    public String getRecipientAddress() {
        return recipientAddress;
    }
    public String getStatus() {
        return status;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OrderResponse that = (OrderResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(courierId, that.courierId) && Objects.equals(senderAddress, that.senderAddress) && Objects.equals(recipientAddress, that.recipientAddress) && Objects.equals(status, that.status) && Objects.equals(price, that.price) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, userId, courierId, senderAddress, recipientAddress, status, price, createdAt, updatedAt);
    }
}