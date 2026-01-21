package org.example.couriers.entitys;

import jakarta.persistence.*;
import org.example.couriers.entitys.enums.CourierStatus;
import org.example.courierscontract.exception.IncorrectDataException;

import java.util.Set;

@Entity
@Table(name = "couriers")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
public class Courier extends User {

    private String deliveryMethod;
    private CourierStatus status;
    private Double latitude;
    private Double longitude;
    private Set<Order> assignedOrders;

    protected Courier() {
        super();
    }

    public Courier(String email, String phone, String name, String deliveryMethod) {
        super(email, phone, name);
        setDeliveryMethod(deliveryMethod);
        setStatus(CourierStatus.FREE);
    }

    @Column(name = "delivery_method", nullable = false)
    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        if (deliveryMethod == null || deliveryMethod.trim().isEmpty()) {
            throw new IncorrectDataException("способ доставки не может быть пустым");
        }
        this.deliveryMethod = deliveryMethod;
    }

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public CourierStatus getStatus() {
        return status;
    }

    public void setStatus(CourierStatus status) {
        if (status == null) {
            throw new IncorrectDataException("статус курьера не может быть пустым");
        }
        this.status = status;
    }

    @Column(name = "latitude")
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            throw new IncorrectDataException("широта должна быть от -90 до 90");
        }
        this.latitude = latitude;
    }

    @Column(name = "longitude")
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            throw new IncorrectDataException("долгота должна быть от -180 до 180");
        }
        this.longitude = longitude;
    }

    @OneToMany(mappedBy = "courier", fetch = FetchType.LAZY)
    public Set<Order> getAssignedOrders() {
        return assignedOrders;
    }
    public void setAssignedOrders(Set<Order> assignedOrders) {
        this.assignedOrders = assignedOrders;
    }
}