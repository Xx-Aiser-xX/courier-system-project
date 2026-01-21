package org.example.couriers.entitys;

import jakarta.persistence.*;
import org.example.courierscontract.exception.IncorrectDataException;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    private UUID id;
    private String email;
    private String phone;
    private String name;
    private boolean deleted;
    private Set<Order> orders;
    private Set<Notification> notifications;


    protected User() {
    }

    public User(String email, String phone, String name) {
        setEmail(email);
        setPhone(phone);
        setName(name);
        setDeleted(false);
    }

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Column(name = "email", nullable = false, unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IncorrectDataException("некорректный формат почты: " + email);
        }
        this.email = email;
    }

    @Column(name = "phone", nullable = false, unique = true)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || !phone.matches("^\\d{11}$")) {
            throw new IncorrectDataException("неверный формат телефона,должен быть 11 символов: " + phone);
        }
        this.phone = phone;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty() || name.length() > 100) {
            throw new IncorrectDataException("имя не должно быть пустым или длиннее 100 символов");
        }
        this.name = name;
    }

    @Column(name = "is_deleted", nullable = false)
    public boolean isDeleted() {
        return deleted;
    }
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    public Set<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(Set<Notification> notifications) {
        this.notifications = notifications;
    }
}