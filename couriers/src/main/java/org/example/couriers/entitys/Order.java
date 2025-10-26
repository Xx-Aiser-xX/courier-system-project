package org.example.couriers.entitys;

import jakarta.persistence.*;
import org.example.couriers.entitys.enums.OrderStatus;
import org.example.couriers.exception.IncorrectDataException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    private User user;
    private Courier courier;
    private String senderAddress;
    private String recipientAddress;
    private OrderStatus status;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Notification> notifications;

    protected Order() {}

    public Order(User user, String senderAddress, String recipientAddress, BigDecimal price) {
        setUser(user);
        setSenderAddress(senderAddress);
        setRecipientAddress(recipientAddress);
        setPrice(price);
        setStatus(OrderStatus.CREATED);
        setCreatedAt(LocalDateTime.now());
        setUpdatedAt(LocalDateTime.now());
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new IncorrectDataException("User cannot be null for an order.");
        }
        this.user = user;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    @Column(name = "sender_address", nullable = false, length = 500)
    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        if (senderAddress == null || senderAddress.trim().isEmpty()) {
            throw new IncorrectDataException("Sender address cannot be empty.");
        }
        this.senderAddress = senderAddress;
    }

    @Column(name = "recipient_address", nullable = false, length = 500)
    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        if (recipientAddress == null || recipientAddress.trim().isEmpty()) {
            throw new IncorrectDataException("Recipient address cannot be empty.");
        }
        this.recipientAddress = recipientAddress;
    }

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        if (status == null) {
            throw new IncorrectDataException("Order status cannot be null.");
        }
        this.status = status;
    }

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IncorrectDataException("Price cannot be null or negative.");
        }
        this.price = price;
    }

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
