package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.dao.CameraRepository;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.exception.CameraNotCreatedException;
import com.onboarding.camera.cameraonboarding.service.impl.CameraServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CameraServiceImplTest {

    @Mock
    private CameraRepository cameraRepository;

    @Mock
    private DateTimeFactory dateTimeFactory;

    @InjectMocks
    private CameraServiceImpl cameraService;

    private Camera camera;

    private final String CAMERA_NAME = "Camera 1";
    private final String FIRMWARE_VERSION = "v1.0";
    private final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 7, 29, 10, 0);

    @BeforeEach
    void setUp() {
        camera = new Camera();
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);
        camera.setCreatedAt(CREATED_AT);
    }

    @Test
    void expect_handleSaveCamera_withValidCamera_returnSavedCamera() {

        // arrange
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);
        when(dateTimeFactory.now()).thenReturn(CREATED_AT);
        when(cameraRepository.save(camera)).thenReturn(camera);

        // act
        Camera savedCamera = cameraService.handleSaveCamera(camera);

        // assert
        Assertions.assertThat(savedCamera).isNotNull();
        Assertions.assertThat(savedCamera.getCreatedAt()).isNotNull();
        Assertions.assertThat(savedCamera.getCreatedAt()).isEqualTo(CREATED_AT);
        Assertions.assertThat(savedCamera.getCameraName()).isNotNull();
        Assertions.assertThat(savedCamera.getCameraName()).isEqualTo(CAMERA_NAME);
        Assertions.assertThat(savedCamera.getFirmwareVersion()).isNotNull();
        Assertions.assertThat(savedCamera.getFirmwareVersion()).isEqualTo(FIRMWARE_VERSION);

        verify(dateTimeFactory).now();
        verify(cameraRepository).save(camera);
    }

    @Test
    void expect_handleSaveCamera_withNullCameraName_throwsException() {

        // arrange
        camera.setCameraName(null);
        when(cameraRepository.save(camera)).thenThrow(new CameraNotCreatedException("Camera name cannot be null"));

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleSaveCamera(camera))
                .isInstanceOf(CameraNotCreatedException.class);
    }

    @Test
    void expect_handleSaveCamera_withNullFirmwareVersion_throwsException() {

        // arrange
        camera.setFirmwareVersion(null);
        when(cameraRepository.save(camera)).thenThrow(new CameraNotCreatedException("Firmware version cannot be null"));

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleSaveCamera(camera))
                .isInstanceOf(CameraNotCreatedException.class);
    }

    @Test
    void expect_handleSaveCamera_withNullCreatedAt_throwsException() {

        // arrange
        camera.setCreatedAt(null);
        when(cameraRepository.save(camera))
                .thenThrow(new CameraNotCreatedException("An error occurred while creating camera"));

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleSaveCamera(camera))
                .isInstanceOf(CameraNotCreatedException.class);

        verify(dateTimeFactory).now();
    }
}