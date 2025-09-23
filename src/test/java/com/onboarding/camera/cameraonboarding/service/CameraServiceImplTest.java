package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.dto.LocationDto;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.entity.Location;
import com.onboarding.camera.cameraonboarding.exception.CameraAlreadyInitializedException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotCreatedException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotInitializedException;
import com.onboarding.camera.cameraonboarding.exception.ImageAlreadyUploadedException;
import com.onboarding.camera.cameraonboarding.exception.ImageNotDownloadedException;
import com.onboarding.camera.cameraonboarding.exception.ImageNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.ImageNotUploadedException;
import com.onboarding.camera.cameraonboarding.exception.LocationNotAddedException;
import com.onboarding.camera.cameraonboarding.repository.CameraRepository;
import com.onboarding.camera.cameraonboarding.service.impl.BlobStorageServiceImpl;
import com.onboarding.camera.cameraonboarding.service.impl.CameraServiceImpl;
import com.onboarding.camera.cameraonboarding.util.DateTimeFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CameraServiceImplTest {

    @Mock
    private CameraRepository cameraRepository;

    @Mock
    private DateTimeFactory dateTimeFactory;

    @Mock
    private BlobStorageServiceImpl blobStorageService;

    @Mock
    private MetricsService metricsService;

    @InjectMocks
    private CameraServiceImpl cameraService;

    @Captor
    ArgumentCaptor<Camera> cameraArgumentCaptor;

    private Camera camera;

    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private LocationDto locationDto;

    private final String CAMERA_NAME = "Camera 1";
    private final String FIRMWARE_VERSION = "v1.0";
    private final UUID CAMERA_ID = UUID.randomUUID();
    private final UUID NON_EXISTING_UUID = UUID.fromString("ef556dc0-0ddc-4f39-a96d-6886a54eee54");
    private final LocalDateTime NOW = LocalDateTime.now();
    private final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 7, 29, 10, 0);
    private final LocalDateTime ONBOARDED_AT = LocalDateTime.of(2024, 7, 29, 10, 0);
    private final LocalDateTime INITIALIZED_AT = LocalDateTime.of(2024, 8, 7, 10, 0);
    private final byte[] IMAGE_DATA = new byte[1024 * 1024];
    private final String CONTAINER_NAME = "test_container";
    private final UUID IMAGE_ID = UUID.randomUUID();
    private final UUID NULL_UUID = null;
    private final Double LATITUDE = 51.232;
    private final Double LONGITUDE = -51.232;
    private final String ADDRESS = "long enough address";

    @BeforeEach
    void setUp() {
        camera = new Camera();
        camera.setCamId(CAMERA_ID);
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);
        camera.setCreatedAt(CREATED_AT);
        camera.setOnboardedAt(ONBOARDED_AT);
        locationDto = new LocationDto();
    }

    @Test
    void expect_handleSaveCamera_withValidCamera_returnSavedCamera() {

        // arrange
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);
        Mockito.when(dateTimeFactory.now()).thenReturn(CREATED_AT);
        Mockito.when(cameraRepository.save(camera)).thenReturn(camera);

        // act
        final Camera savedCamera = cameraService.handleSaveCamera(camera);

        // assert
        Assertions.assertThat(savedCamera)
                .isNotNull()
                .satisfies(cam -> {
                    Assertions.assertThat(cam.getCreatedAt())
                            .as("Check Created At")
                            .isNotNull()
                            .isEqualTo(CREATED_AT);

                    Assertions.assertThat(cam.getCameraName())
                            .as("Check Camera Name")
                            .isNotNull()
                            .isEqualTo(CAMERA_NAME);

                    Assertions.assertThat(cam.getFirmwareVersion())
                            .as("Check Fırmware Version")
                            .isNotNull()
                            .isEqualTo(FIRMWARE_VERSION);
                });

        Mockito.verify(dateTimeFactory, Mockito.times(2)).now();
        Mockito.verify(cameraRepository).save(camera);
    }

    @Test
    void expect_handleSaveCamera_withNullCameraName_throwsException() {

        // arrange
        camera.setCameraName(null);
        Mockito.when(cameraRepository.save(camera)).thenThrow(new CameraNotCreatedException("Camera name cannot be null"));

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleSaveCamera(camera))
                .isInstanceOf(CameraNotCreatedException.class);

        Mockito.verify(cameraRepository).save(camera);
    }

    @Test
    void expect_handleSaveCamera_withNullFirmwareVersion_throwsException() {

        // arrange
        camera.setFirmwareVersion(null);
        Mockito.when(cameraRepository.save(camera)).thenThrow(new CameraNotCreatedException("Firmware version cannot be null"));

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleSaveCamera(camera))
                .isInstanceOf(CameraNotCreatedException.class);

        Mockito.verify(cameraRepository).save(camera);
    }

    @Test
    void expect_handleSaveCamera_withNullCreatedAt_throwsException() {

        // arrange
        camera.setCreatedAt(null);
        Mockito.when(cameraRepository.save(camera))
                .thenThrow(new CameraNotCreatedException("An error occurred while creating camera"));

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleSaveCamera(camera))
                .isInstanceOf(CameraNotCreatedException.class);

        Mockito.verify(dateTimeFactory, Mockito.times(2)).now();
    }

    @Test
    void expect_handleInitializeCamera_withValidCamera_returnVoid() {

        // arrange
        camera.setCamId(CAMERA_ID);
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));
        Mockito.when(dateTimeFactory.now()).thenReturn(NOW);
        Mockito.when(cameraRepository.save(camera)).thenReturn(camera);

        // act
        cameraService.handleInitializeCamera(CAMERA_ID);

        // assert
        Mockito.verify(cameraRepository).save(cameraArgumentCaptor.capture());
        final Camera capturedCamera = cameraArgumentCaptor.getValue();

        Assertions.assertThat(capturedCamera.getInitializedAt()).isNotNull();
        Assertions.assertThat(capturedCamera.getInitializedAt()).isEqualTo(NOW);

        Mockito.verify(cameraRepository).findById(CAMERA_ID);
    }

    @Test
    void expect_handleInitializeCamera_withNonExistingCamId_throwsException() {

        // arrange
        Mockito.when(cameraRepository.findById(NON_EXISTING_UUID)).thenReturn(Optional.empty());

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleInitializeCamera(NON_EXISTING_UUID))
                .isInstanceOf(CameraNotFoundException.class)
                .hasMessageContaining("Camera not found with id: " + NON_EXISTING_UUID);

        Mockito.verify(cameraRepository).findById(NON_EXISTING_UUID);
    }

    @Test
    void expect_handleInitializeCamera_withAlreadyInitializedCamera_throwsException() {

        // arrange
        camera.setInitializedAt(INITIALIZED_AT);
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleInitializeCamera(CAMERA_ID))
                .isInstanceOf(CameraAlreadyInitializedException.class)
                .hasMessageContaining("Camera already initialized");

        Mockito.verify(cameraRepository).findById(CAMERA_ID);
        Mockito.verify(cameraRepository, Mockito.never()).save(ArgumentMatchers.any(Camera.class));
    }

    @Test
    void expect_handleInitializeCamera_withNotInitializedCamera_throwsException() {

        // arrange
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));

        Mockito.doThrow(new RuntimeException("Error occurred while initializing camera"))
                .when(cameraRepository).save(camera);

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleInitializeCamera(CAMERA_ID))
                .isInstanceOf(CameraNotInitializedException.class);

        Mockito.verify(cameraRepository).findById(CAMERA_ID);
    }

    @Test
    void expect_getCameraById_toGetCamera() {
        // arrange
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));

        // act
        final Camera actualCamera = cameraService.getCameraById(CAMERA_ID);

        // assert
        Mockito.verify(cameraRepository).findById(CAMERA_ID);
        Assertions.assertThat(actualCamera).isEqualTo(camera);
    }

    @Test
    void expect_getCameraById_withNonExistingUUID_toThrowCameraNotFoundException() {

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.getCameraById(NON_EXISTING_UUID))
                .isInstanceOf(CameraNotFoundException.class);

        Mockito.verify(cameraRepository, Mockito.never()).findById(CAMERA_ID);
    }

    @Test
    void expect_handleUploadImage_withValidData_returnVoid() {
        // arrange
        camera.setInitializedAt(INITIALIZED_AT);
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));
        Mockito.when(blobStorageService.getContainerName()).thenReturn(CONTAINER_NAME);

        // act
        cameraService.handleUploadImage(CAMERA_ID, IMAGE_ID, IMAGE_DATA);

        // assert
        Mockito.verify(blobStorageService).uploadFile(CONTAINER_NAME, IMAGE_ID.toString(), IMAGE_DATA);
        Mockito.verify(cameraRepository).save(camera);
        Assertions.assertThat(camera.getImageId()).isEqualTo(IMAGE_ID);
    }

    @Test
    void expect_handleUploadImage_withAlreadyUploadedImage_throwsException() {
        // arrange
        camera.setImageId(IMAGE_ID);
        camera.setInitializedAt(INITIALIZED_AT);
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleUploadImage(CAMERA_ID, IMAGE_ID, IMAGE_DATA))
                .isInstanceOf(ImageAlreadyUploadedException.class)
                .hasMessageContaining("Camera already have image with id: " + IMAGE_ID);

        Mockito.verify(blobStorageService, Mockito.never()).uploadFile(Mockito.anyString(), Mockito.anyString(), Mockito.any());
    }

    @Test
    void expect_handleUploadImage_withNonExistingCamera_throwsException() {
        // arrange
        camera.setInitializedAt(INITIALIZED_AT);
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.empty());

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleUploadImage(CAMERA_ID, IMAGE_ID, IMAGE_DATA))
                .isInstanceOf(CameraNotFoundException.class)
                .hasMessageContaining("Camera not found with id: " + CAMERA_ID);

        Mockito.verify(blobStorageService, Mockito.never()).uploadFile(Mockito.anyString(), Mockito.anyString(), Mockito.any());
    }

    @Test
    void expect_handleUploadImage_withNotInitializedCamera_throwsException() {
        // arrange
        camera.setInitializedAt(null);
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleUploadImage(CAMERA_ID, IMAGE_ID, IMAGE_DATA))
                .isInstanceOf(CameraNotInitializedException.class)
                .hasMessageContaining("Camera is not initialized with id: " + CAMERA_ID);

        Mockito.verify(blobStorageService, Mockito.never()).uploadFile(Mockito.anyString(), Mockito.anyString(), Mockito.any());
    }

    @Test
    void expect_handleUploadImage_withBlobStorageError_throwsException() {
        // arrange
        camera.setInitializedAt(INITIALIZED_AT);
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));
        Mockito.when(blobStorageService.getContainerName()).thenReturn(CONTAINER_NAME);
        Mockito.doThrow(new RuntimeException("Blob storage error")).when(blobStorageService).uploadFile(CONTAINER_NAME, IMAGE_ID.toString(), IMAGE_DATA);

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleUploadImage(CAMERA_ID, IMAGE_ID, IMAGE_DATA))
                .isInstanceOf(ImageNotUploadedException.class)
                .hasMessageContaining("Error occurred while uploading image");

        Mockito.verify(cameraRepository, Mockito.never()).save(camera);
    }

    @Test
    void expect_handleUploadImage_withNullImageId_toThrowIllegalArgumentException() {
        // arrange
        camera.setInitializedAt(INITIALIZED_AT);
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleUploadImage(CAMERA_ID, NULL_UUID, IMAGE_DATA))
                .isInstanceOf(ImageNotUploadedException.class);

        Mockito.verify(cameraRepository, Mockito.never()).save(camera);
    }

    @Test
    void expect_handleDownloadImage_withValidData_returnImageBytes() {
        // arrange
        camera.setImageId(IMAGE_ID);
        camera.setInitializedAt(INITIALIZED_AT);
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));
        Mockito.when(blobStorageService.getContainerName()).thenReturn(CONTAINER_NAME);

        Mockito.lenient().doAnswer(invocation -> {
                    outputStream = invocation.getArgument(0);
                    outputStream.writeBytes(IMAGE_DATA);
                    return null;
                }
        ).when(blobStorageService).getBlob(
                Mockito.any(outputStream.getClass()), Mockito.eq(CONTAINER_NAME), Mockito.eq(IMAGE_ID.toString()));

        // act
        byte[] downloadedImage = cameraService.handleDownloadImage(CAMERA_ID);

        // assert
        Assertions.assertThat(downloadedImage).isEqualTo(IMAGE_DATA);
        Mockito.verify(blobStorageService).getBlob(
                Mockito.any(ByteArrayOutputStream.class), Mockito.eq(CONTAINER_NAME), Mockito.eq(IMAGE_ID.toString()));
    }

    @Test
    void expect_handleDownloadImage_withNonExistingCamera_throwsException() {
        // arrange
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.empty());

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleDownloadImage(CAMERA_ID))
                .isInstanceOf(CameraNotFoundException.class)
                .hasMessageContaining("Camera not found with id: " + CAMERA_ID);

        Mockito.verify(blobStorageService, Mockito.never()).getBlob(outputStream, CONTAINER_NAME, IMAGE_ID.toString());
    }

    @Test
    void expect_handleDownloadImage_withNonExistingImage_throwsException() {
        // arrange
        camera.setImageId(null);
        camera.setInitializedAt(INITIALIZED_AT);
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleDownloadImage(CAMERA_ID))
                .isInstanceOf(ImageNotFoundException.class)
                .hasMessageContaining("Image is not found by given cameraId: " + CAMERA_ID);

        Mockito.verify(blobStorageService, Mockito.never()).getBlob(outputStream, CONTAINER_NAME, IMAGE_ID.toString());
    }

    @Test
    void expect_handleDownloadImage_withBlobStorageError_throwsException() {
        // arrange
        camera.setImageId(IMAGE_ID);
        camera.setInitializedAt(INITIALIZED_AT);
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));
        Mockito.when(blobStorageService.getContainerName()).thenReturn(CONTAINER_NAME);
        Mockito.doThrow(new RuntimeException("Blob storage error")).when(blobStorageService).getBlob(outputStream, CONTAINER_NAME, IMAGE_ID.toString());

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleDownloadImage(CAMERA_ID))
                .isInstanceOf(ImageNotDownloadedException.class)
                .hasMessageContaining("Error occurred while downloading image");

        Mockito.verify(cameraRepository).findById(CAMERA_ID);
    }

    @Test
    void expect_handleDownloadImage_withNotInitializedCamera_throwsException() {
        // arrange
        camera.setInitializedAt(null);
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleDownloadImage(CAMERA_ID))
                .isInstanceOf(CameraNotInitializedException.class)
                .hasMessageContaining("Camera is not initialized with id: " + CAMERA_ID);

        Mockito.verify(blobStorageService, Mockito.never()).getBlob(Mockito.any(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void expect_handleAddLocation_withValidLocation_returnCamera() {
        // arrange
        locationDto.setLatitude(LATITUDE);
        locationDto.setLongitude(LONGITUDE);
        locationDto.setAddress(ADDRESS);
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));

        // act
        Camera updatedCamera = cameraService.handleAddLocation(CAMERA_ID, locationDto);
        Location location = updatedCamera.getLocation();

        // assert
        Assertions.assertThat(location)
                .isNotNull()
                .satisfies(loc -> {
                    Assertions.assertThat(loc.getLatitude())
                            .as("Check Latitude")
                            .isNotNull()
                            .isEqualTo(LATITUDE);

                    Assertions.assertThat(loc.getLongitude())
                            .as("Check Longitude")
                            .isNotNull()
                            .isEqualTo(LONGITUDE);

                    Assertions.assertThat(loc.getAddress())
                            .as("Check Address")
                            .isNotNull()
                            .isEqualTo(ADDRESS);
                });

        Mockito.verify(cameraRepository).save(camera);
        Mockito.verify(cameraRepository).findById(CAMERA_ID);
    }

    @Test
    void expect_handleAddLocation_withNonExistingCamera_throwsException() {
        // arrange
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.empty());

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleAddLocation(CAMERA_ID, locationDto))
                .isInstanceOf(CameraNotFoundException.class)
                .hasMessageContaining("Camera not found with id: " + CAMERA_ID);

        Mockito.verify(cameraRepository).findById(CAMERA_ID);
    }

    @Test
    void expect_handleAddLocation_withLocationNotAddedError_throwsException() {
        // arrange
        Mockito.when(cameraRepository.findById(CAMERA_ID)).thenReturn(Optional.of(camera));
        Mockito.doThrow(new LocationNotAddedException("Error occurred while adding location"))
                .when(cameraRepository).save(camera);

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraService.handleAddLocation(CAMERA_ID, locationDto))
                .isInstanceOf(LocationNotAddedException.class)
                .hasMessageContaining("Error occurred while adding location");

        Mockito.verify(cameraRepository).findById(CAMERA_ID);
    }
}
