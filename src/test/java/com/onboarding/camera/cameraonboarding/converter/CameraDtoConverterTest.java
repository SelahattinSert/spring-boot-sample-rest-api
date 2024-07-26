package com.onboarding.camera.cameraonboarding.converter;

import com.onboarding.camera.cameraonboarding.dto.CameraDto;
import com.onboarding.camera.cameraonboarding.dto.CameraResponse;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
        CameraDto cameraDto = new CameraDto();
        cameraDto.setCameraName(CAMERA_NAME);
        cameraDto.setFirmwareVersion(FIRMWARE_VERSION);

        // act
        Camera camera = cameraDtoConverter.toEntity(cameraDto);

        // assert
        assertThat(camera).isNotNull();
        assertThat(camera.getCameraName()).isEqualTo(CAMERA_NAME);
        assertThat(camera.getFirmwareVersion()).isEqualTo(FIRMWARE_VERSION);
    }

    @Test
    void expect_convert_withValidCamera_returnCameraDto() {
        // arrange
        Camera camera = new Camera();
        camera.setCamId(CAMERA_ID);
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);

        // act
        CameraResponse response = cameraDtoConverter.toCameraResponse(camera);

        // assert
        assertThat(response).isNotNull();
        assertThat(response.getCameraId()).isEqualTo(CAMERA_ID);
        assertThat(response.getCameraName()).isEqualTo(CAMERA_NAME);
        assertThat(response.getFirmwareVersion()).isEqualTo(FIRMWARE_VERSION);
    }
}