package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.exception.CameraAlreadyInitializedException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotInitializedException;
import com.onboarding.camera.cameraonboarding.repository.CameraRepository;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.exception.CameraNotCreatedException;
import com.onboarding.camera.cameraonboarding.service.impl.CameraServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
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

    @Captor
    ArgumentCaptor<Camera> cameraArgumentCaptor;

    private Camera camera;

    private final String CAMERA_NAME = "Camera 1";
    private final String FIRMWARE_VERSION = "v1.0";
    private final UUID CAMERA_ID = UUID.randomUUID();
    private final UUID NON_EXISTING_UUID = UUID.fromString("ef556dc0-0ddc-4f39-a96d-6886a54eee54");
    private final LocalDateTime NOW = LocalDateTime.now();
    private final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 7, 29, 10, 0);
    private final LocalDateTime INITIALIZED_AT = LocalDateTime.of(2024, 8, 7, 10, 0);

    @BeforeEach
    void setUp() {
        camera = new Camera();
        camera.setCamId(CAMERA_ID);
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

    @Test
    void expect_handleInitializeCamera_withValidCamera_returnVoid() {

        // arrange
        camera.setCamId(CAMERA_ID);
        when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));
        when(dateTimeFactory.now()).thenReturn(NOW);
        when(cameraRepository.save(camera)).thenReturn(camera);

        // act
        cameraService.handleInitializeCamera(CAMERA_ID);

        // assert
        verify(cameraRepository).save(cameraArgumentCaptor.capture());
        Camera capturedCamera = cameraArgumentCaptor.getValue();

        Assertions.assertThat(capturedCamera.getInitializedAt()).isNotNull();
        Assertions.assertThat(capturedCamera.getInitializedAt()).isEqualTo(NOW);

        verify(cameraRepository).findById(CAMERA_ID);
    }

    @Test
    void expect_handleInitializeCamera_withNonExistingCamId_throwsException() {

        // arrange
        when(cameraRepository.findById(NON_EXISTING_UUID)).thenReturn(Optional.empty());

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleInitializeCamera(NON_EXISTING_UUID))
                .isInstanceOf(CameraNotFoundException.class)
                .hasMessageContaining("Camera not found with id: " + NON_EXISTING_UUID);

        verify(cameraRepository).findById(NON_EXISTING_UUID);
    }

    @Test
    void expect_handleInitializeCamera_withAlreadyInitializedCamera_throwsException() {

        // arrange
        camera.setInitializedAt(INITIALIZED_AT);
        when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleInitializeCamera(CAMERA_ID))
                .isInstanceOf(CameraAlreadyInitializedException.class)
                .hasMessageContaining("Camera already initialized");

        verify(cameraRepository).findById(CAMERA_ID);
    }

    @Test
    void expect_handleInitializeCamera_withNotInitializedCamera_throwsException() {

        // arrange
        when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));

        Mockito.doThrow(new CameraNotInitializedException("Error occurred while initializing camera"))
                .when(cameraRepository).save(camera);

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleInitializeCamera(CAMERA_ID))
                .isInstanceOf(CameraNotInitializedException.class);

        verify(cameraRepository).findById(CAMERA_ID);
    }
}