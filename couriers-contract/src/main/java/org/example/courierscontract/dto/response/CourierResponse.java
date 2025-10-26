package org.example.courierscontract.dto.response;

import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;
import java.util.UUID;

public class CourierResponse extends RepresentationModel<CourierResponse> {
    private final UUID id;
    private final String name;
    private final String phone;
    private final String deliveryMethod;
    private final String status;
    private final Double latitude;
    private final Double longitude;

    public CourierResponse(UUID id, String name, String phone, String deliveryMethod, String status, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.deliveryMethod = deliveryMethod;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    public String getDeliveryMethod() {
        return deliveryMethod;
    }
    public String getStatus() {
        return status;
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CourierResponse that = (CourierResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(phone, that.phone) && Objects.equals(deliveryMethod, that.deliveryMethod) && Objects.equals(status, that.status) && Objects.equals(latitude, that.latitude) && Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, phone, deliveryMethod, status, latitude, longitude);
    }
}