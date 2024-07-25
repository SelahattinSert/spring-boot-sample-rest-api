package com.onboarding.camera.cameraonboarding.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@ToString
@Entity
@Table(name = "camera_metadata")
public class Camera {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "cam_id")
    private UUID camId;

    @Column(name = "image_id")
    private  UUID imageId;

    @Column(name = "camera_name")
    private String cameraName;

    @Column(name = "firmware_version")
    private String firmwareVersion;

    @Column(name = "container_name")
    private String containerName;

    @Column(name = "name_of_stored_picture")
    private String nameOfStoredPicture;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "onboarded_at")
    private LocalDateTime onboardedAt;

    @Column(name = "initialized_at")
    private LocalDateTime initializedAt;
}
