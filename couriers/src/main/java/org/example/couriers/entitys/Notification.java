package org.example.couriers.entitys;

import jakarta.persistence.*;
import org.example.courierscontract.exception.IncorrectDataException;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    private User user;
    private Order order;
    private String message;
    private LocalDateTime notificationDateTime;

    protected Notification() {}

    public Notification(User user, Order order, String message) {
        setUser(user);
        setOrder(order);
        setMessage(message);
        setNotificationDateTime(LocalDateTime.now());
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Column(name = "message", nullable = false, length = 1000)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IncorrectDataException("Уведомление не может быть пустым");
        }
        this.message = message;
    }

    @Column(name = "notification_date_time", nullable = false)
    public LocalDateTime getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(LocalDateTime notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }
}