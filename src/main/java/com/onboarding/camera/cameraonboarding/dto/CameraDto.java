package com.onboarding.camera.cameraonboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CameraDto {
    private String cameraName;
    private String firmwareVersion;
}
