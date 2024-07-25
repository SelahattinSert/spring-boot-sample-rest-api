package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.dao.CameraRepository;
import com.onboarding.camera.cameraonboarding.dto.CameraDto;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.exception.CameraNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CameraServiceImplTest {

    @Mock
    private CameraRepository cameraRepository;

    @InjectMocks
    private CameraServiceImpl cameraService;

    private Camera camera;

    @BeforeEach
    void setUp() {
        camera = new Camera();
        camera.setCameraName("Camera 1");
        camera.setFirmwareVersion("v1.0");
    }

    @Test
    void expect_handleSaveCamera_withValidCamera_returnSavedCamera() {

        // arrange
        CameraDto cameraDto = new CameraDto();
        cameraDto.setCameraName(camera.getCameraName());
        cameraDto.setFirmwareVersion(camera.getFirmwareVersion());

        // act
        when(cameraRepository.save(Mockito.any(Camera.class))).thenReturn(camera);

        Camera savedCamera = cameraService.handleSaveCamera(camera);

        // assert
        Assertions.assertThat(savedCamera).isNotNull();
    }

    @Test
    void expect_handleSaveCamera_withNullCameraName_throwsException() {

        // arrange
        camera.setCameraName(null);

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleSaveCamera(camera))
                .isInstanceOf(CameraNotFoundException.class);
    }

    @Test
    void expect_handleSaveCamera_withNullFirmwareVersion_throwsException() {

        // arrange
        camera.setFirmwareVersion(null);

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleSaveCamera(camera))
                .isInstanceOf(CameraNotFoundException.class);
    }
}