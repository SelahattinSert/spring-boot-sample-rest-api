package com.onboarding.camera.cameraonboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CameraDto {

    @NotBlank(message = "Camera name cannot be blank")
    @Size(min = 2, max = 100, message = "Camera name should be up to 100 characters")
    private String cameraName;

    @NotBlank(message = "Firmware version cannot be blank")
    @Size(min = 2, max = 100, message = "Firmware version should be up to 100 characters")
    private String firmwareVersion;
}
