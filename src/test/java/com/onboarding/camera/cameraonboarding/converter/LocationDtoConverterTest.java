package com.onboarding.camera.cameraonboarding.converter;

import com.onboarding.camera.cameraonboarding.dto.LocationResponse;
import com.onboarding.camera.cameraonboarding.entity.Location;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocationDtoConverterTest {

    private LocationDtoConverter locationDtoConverter;

    private final Double LATITUDE = 51.232;
    private final Double LONGITUDE = -51.232;
    private final String ADDRESS = "long enough address";

    @BeforeEach
    void setUp() {
        locationDtoConverter = new LocationDtoConverter();
    }

    @Test
    void expect_convert_withValidLocation_returnLocationResponse() {
        // arrange
        Location location = new Location();
        location.setLatitude(LATITUDE);
        location.setLongitude(LONGITUDE);
        location.setAddress(ADDRESS);

        // act
        LocationResponse response = locationDtoConverter.toLocationResponse(location);

        // assert
        AssertionsForClassTypes.assertThat(response).isNotNull();
        AssertionsForClassTypes.assertThat(response.getLatitude()).isEqualTo(LATITUDE);
        AssertionsForClassTypes.assertThat(response.getLongitude()).isEqualTo(LONGITUDE);
        AssertionsForClassTypes.assertThat(response.getAddress()).isEqualTo(ADDRESS);
    }
}