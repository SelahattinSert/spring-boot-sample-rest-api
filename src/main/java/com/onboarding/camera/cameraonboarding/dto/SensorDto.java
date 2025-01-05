package com.onboarding.camera.cameraonboarding.dto;

import com.onboarding.camera.cameraonboarding.enums.SensorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SensorDto {

    @NotBlank(message = "Sensor name cannot be blank")
    @Size(min = 2, max = 100, message = "Sensor name should be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Version cannot be blank")
    @Size(min = 2, max = 50, message = "Version should be between 2 and 50 characters")
    private String version;

    @NotNull(message = "Sensor type cannot be null")
    private SensorType sensorType;

    private String data;
}
