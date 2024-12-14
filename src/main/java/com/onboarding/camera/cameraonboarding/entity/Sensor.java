package com.onboarding.camera.cameraonboarding.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@Entity
@ToString(exclude = "camera")
@Table(name = "sensor_metadata", indexes = @Index(name = "idx_camera_id", columnList = "camera_id"))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "sensor_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Sensor {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "camera_id", nullable = false)
    @JsonBackReference
    private Camera camera;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "version")
    private String version;

    @Column(name = "data")
    private String data;

    @Column(name = "sensor_type", nullable = false, updatable = false, insertable = false)
    @Enumerated(EnumType.STRING)
    private SensorType sensorType;
}
