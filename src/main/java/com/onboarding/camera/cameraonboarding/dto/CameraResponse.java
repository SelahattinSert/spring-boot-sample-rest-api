package com.onboarding.camera.cameraonboarding.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CameraResponse {
    private UUID cameraId;
    private String cameraName;
    private String firmwareVersion;
}
