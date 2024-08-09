package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.exception.CameraAlreadyInitializedException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotCreatedException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotInitializedException;
import com.onboarding.camera.cameraonboarding.repository.CameraRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
    private final String TEST_ZONE_ID = "UTC";

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
        assertThat(savedCamera).isNotNull();
        assertThat(savedCamera.getCreatedAt()).isNotNull();
        assertThat(savedCamera.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(savedCamera.getCameraName()).isNotNull();
        assertThat(savedCamera.getCameraName()).isEqualTo(CAMERA_NAME);
        assertThat(savedCamera.getFirmwareVersion()).isNotNull();
        assertThat(savedCamera.getFirmwareVersion()).isEqualTo(FIRMWARE_VERSION);

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

        assertThat(capturedCamera.getInitializedAt()).isNotNull();
        assertThat(capturedCamera.getInitializedAt()).isEqualTo(NOW);

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
        // you need to verify the save method never called
        verify(cameraRepository, never()).save(any(Camera.class));
    }

    @Test
    void expect_handleInitializeCamera_withNotInitializedCamera_throwsException() {

        // arrange
        when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));

        Mockito.doThrow(new RuntimeException("Error occurred while initializing camera"))
                .when(cameraRepository).save(camera);

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleInitializeCamera(CAMERA_ID))
                .isInstanceOf(CameraNotInitializedException.class);

        verify(cameraRepository).findById(CAMERA_ID);
    }

    @Test
    void expect_getCameraById_toGetCamera() {
        // arrange
        when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));

        // act
        Camera actualCamera = cameraService.getCameraById(CAMERA_ID);

        // assert
        verify(cameraRepository).findById(CAMERA_ID);
        assertThat(actualCamera).isEqualTo(camera);
    }

    @Test
    void expect_getCameraById_withNullCameraId_toThrowIllegalArgumentException() {

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.getCameraById(null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(cameraRepository, never()).findById(CAMERA_ID);
    }
}
