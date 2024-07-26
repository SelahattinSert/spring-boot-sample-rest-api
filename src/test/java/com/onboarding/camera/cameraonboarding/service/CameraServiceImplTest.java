package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.dao.CameraRepository;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.exception.CameraNotCreatedException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CameraServiceImplTest {

    @Mock
    private CameraRepository cameraRepository;

    @InjectMocks
    private CameraServiceImpl cameraService;

    private Camera camera;

    private final String CAMERA_NAME = "Camera 1";
    private final String FIRMWARE_VERSION = "v1.0";

    @BeforeEach
    void setUp() {
        camera = new Camera();
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);
    }

    @Test
    void expect_handleSaveCamera_withValidCamera_returnSavedCamera() {

        // arrange
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);
        when(cameraRepository.save(camera)).thenReturn(camera);

        // act
        Camera savedCamera = cameraService.handleSaveCamera(camera);

        // assert
        Assertions.assertThat(savedCamera).isNotNull();

        // verify
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

        // verify
        verify(cameraRepository, never()).save(Mockito.any(Camera.class));
    }

    @Test
    void expect_handleSaveCamera_withNullFirmwareVersion_throwsException() {

        // arrange
        camera.setFirmwareVersion(null);
        when(cameraRepository.save(camera)).thenThrow(new CameraNotCreatedException("Firmware version cannot be null"));

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleSaveCamera(camera))
                .isInstanceOf(CameraNotCreatedException.class);

        // verify
        verify(cameraRepository, never()).save(Mockito.any(Camera.class));
    }
}