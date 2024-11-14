package com.onboarding.camera.cameraonboarding.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@ToString(exclude = "camera")
@Entity
@Table(name = "location")
public class Location {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "location_id")
    private UUID locationId;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "address", nullable = false)
    private String address;

    @OneToOne
    @JoinColumn(name = "camera_id", referencedColumnName = "cam_id")
    @JsonBackReference
    private Camera camera;
}
