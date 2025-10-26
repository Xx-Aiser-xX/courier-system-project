package org.example.couriers.entitys;

import jakarta.persistence.*;
import org.example.couriers.entitys.enums.CourierStatus;
import org.example.couriers.exception.IncorrectDataException;

import java.util.List;

@Entity
@Table(name = "couriers")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
public class Courier extends User {

    private String deliveryMethod;
    private CourierStatus status;
    private Double latitude;
    private Double longitude;
    private List<Order> assignedOrders;

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
            throw new IncorrectDataException("Delivery method cannot be empty.");
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
            throw new IncorrectDataException("Courier status cannot be null.");
        }
        this.status = status;
    }

    @Column(name = "latitude")
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            throw new IncorrectDataException("Latitude must be between -90 and 90.");
        }
        this.latitude = latitude;
    }

    @Column(name = "longitude")
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            throw new IncorrectDataException("Longitude must be between -180 and 180.");
        }
        this.longitude = longitude;
    }

    @OneToMany(mappedBy = "courier", fetch = FetchType.LAZY)
    public List<Order> getAssignedOrders() {
        return assignedOrders;
    }

    public void setAssignedOrders(List<Order> assignedOrders) {
        this.assignedOrders = assignedOrders;
    }
}