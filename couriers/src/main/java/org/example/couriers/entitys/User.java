package org.example.couriers.entitys;

import jakarta.persistence.*;
import org.example.couriers.exception.IncorrectDataException;

import java.util.List;
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends BaseEntity {
    private String email;
    private String phone;
    private String name;
    private List<Order> orders;
    private List<Notification> notifications;


    protected User() {
    }

    public User(String email, String phone, String name) {
        setEmail(email);
        setPhone(phone);
        setName(name);
    }

    @Column(name = "email", nullable = false, unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IncorrectDataException("Invalid email format: " + email);
        }
        this.email = email;
    }

    @Column(name = "phone", nullable = false, unique = true)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || !phone.matches("^\\d{11}$")) {
            throw new IncorrectDataException("Invalid phone format (must be 11 digits): " + phone);
        }
        this.phone = phone;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty() || name.length() > 100) {
            throw new IncorrectDataException("Name cannot be empty or longer than 100 characters.");
        }
        this.name = name;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}