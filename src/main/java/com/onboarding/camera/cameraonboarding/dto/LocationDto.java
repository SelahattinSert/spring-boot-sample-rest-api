package com.onboarding.camera.cameraonboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {

    @NotBlank(message = "Latitude cannot be blank")
    private double latitude;

    @NotBlank(message = "Longitude cannot be blank")
    private double longitude;

    @NotBlank(message = "Address version cannot be blank")
    @Size(min = 10, max = 100, message = "Address should be up to 100 characters")
    private String address;
}
