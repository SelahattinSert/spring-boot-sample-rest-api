package com.onboarding.camera.cameraonboarding.converter;

import com.onboarding.camera.cameraonboarding.dto.LocationResponse;
import com.onboarding.camera.cameraonboarding.entity.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationDtoConverter {

    public LocationResponse toLocationResponse(Location location) {
        LocationResponse response = new LocationResponse();
        response.setLatitude(location.getLatitude());
        response.setLongitude(location.getLongitude());
        response.setAddress(location.getAddress());
        return response;
    }
}
