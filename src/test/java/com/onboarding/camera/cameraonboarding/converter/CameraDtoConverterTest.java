package com.onboarding.camera.cameraonboarding.converter;

import com.onboarding.camera.cameraonboarding.dto.CameraDto;
import com.onboarding.camera.cameraonboarding.dto.CameraResponse;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class CameraDtoConverterTest {

    private CameraDtoConverter cameraDtoConverter;

    private final String CAMERA_NAME = "Camera 1";
    private final String FIRMWARE_VERSION = "v1.0";
    private final UUID CAMERA_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        cameraDtoConverter = new CameraDtoConverter();
    }

    @Test
    void expect_convert_withValidCameraDto_returnCamera() {
        // arrange
        final CameraDto cameraDto = new CameraDto();
        cameraDto.setCameraName(CAMERA_NAME);
        cameraDto.setFirmwareVersion(FIRMWARE_VERSION);

        // act
        final Camera camera = cameraDtoConverter.toEntity(cameraDto);

        // assert
        AssertionsForClassTypes.assertThat(camera).isNotNull();
        AssertionsForClassTypes.assertThat(camera.getCameraName()).isEqualTo(CAMERA_NAME);
        AssertionsForClassTypes.assertThat(camera.getFirmwareVersion()).isEqualTo(FIRMWARE_VERSION);
    }

    @Test
    void expect_convert_withValidCamera_returnCameraDto() {
        // arrange
        final Camera camera = new Camera();
        camera.setCamId(CAMERA_ID);
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);

        // act
        final CameraResponse response = cameraDtoConverter.toCameraResponse(camera);

        // assert
        AssertionsForClassTypes.assertThat(response).isNotNull();
        AssertionsForClassTypes.assertThat(response.getCameraId()).isEqualTo(CAMERA_ID);
        AssertionsForClassTypes.assertThat(response.getCameraName()).isEqualTo(CAMERA_NAME);
        AssertionsForClassTypes.assertThat(response.getFirmwareVersion()).isEqualTo(FIRMWARE_VERSION);
    }
}