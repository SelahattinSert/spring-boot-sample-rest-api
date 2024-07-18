package com.onboarding.camera.cameraonboarding.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "CameraMetadata")
public class Camera {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
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

    public UUID getCamId() {
        return camId;
    }

    public void setCamId(UUID camId) {
        this.camId = camId;
    }

    public UUID getImageId() {
        return imageId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getNameOfStoredPicture() {
        return nameOfStoredPicture;
    }

    public void setNameOfStoredPicture(String nameOfStoredPicture) {
        this.nameOfStoredPicture = nameOfStoredPicture;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getOnboardedAt() {
        return onboardedAt;
    }

    public void setOnboardedAt(LocalDateTime onboardedAt) {
        this.onboardedAt = onboardedAt;
    }

    public LocalDateTime getInitializedAt() {
        return initializedAt;
    }

    public void setInitializedAt(LocalDateTime initializedAt) {
        this.initializedAt = initializedAt;
    }

    @Override
    public String toString() {
        return "Camera{" +
                "camId=" + camId +
                ", imageId=" + imageId +
                ", cameraName='" + cameraName + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", containerName='" + containerName + '\'' +
                ", nameOfStoredPicture='" + nameOfStoredPicture + '\'' +
                ", createdAt=" + createdAt +
                ", onboardedAt=" + onboardedAt +
                ", initializedAt=" + initializedAt +
                '}';
    }
}
