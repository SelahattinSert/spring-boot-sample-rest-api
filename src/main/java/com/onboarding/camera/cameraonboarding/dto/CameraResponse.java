package com.onboarding.camera.cameraonboarding.dto;

import lombok.Data;

@Data
public class CameraResponse {
    private String message;
    private String cameraName;
    private String firmwareVersion;
}
