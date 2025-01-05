package com.onboarding.camera.cameraonboarding.dto;

import com.onboarding.camera.cameraonboarding.enums.SensorType;
import lombok.Data;

import java.util.UUID;

@Data
public class SensorResponse {
    private UUID id;
    private String name;
    private String version;
    private SensorType sensorType;
    private String data;
}
