package com.onboarding.camera.cameraonboarding.dto;

import lombok.Data;

@Data
public class LocationResponse {
    private double latitude;
    private double longitude;
    private String address;
}
