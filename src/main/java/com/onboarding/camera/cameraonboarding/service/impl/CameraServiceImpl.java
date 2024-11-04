package com.onboarding.camera.cameraonboarding.service.impl;

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
import com.onboarding.camera.cameraonboarding.service.BlobStorageService;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import com.onboarding.camera.cameraonboarding.util.DateTimeFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CameraServiceImpl implements CameraService {

    private final CameraRepository cameraRepository;

    private final DateTimeFactory dateTimeFactory;

    private final BlobStorageService blobStorageService;

    @Override
    public Camera handleSaveCamera(Camera camera) {

        try {
            camera.setCreatedAt(dateTimeFactory.now());
            camera.setOnboardedAt(dateTimeFactory.now());
            Camera savedCamera = cameraRepository.save(camera);
            log.info("Camera saved with ID: {}", savedCamera.getCamId());
            return savedCamera;
        } catch (Exception ex) {
            log.error("Exception occurred while saving camera: {}", ex.getMessage());
            throw new CameraNotCreatedException(String.format("Error occurred while saving camera: %s", ex.getMessage()));
        }
    }

    @Override
    public void handleInitializeCamera(UUID cameraId) {
        Camera camera = getCameraById(cameraId);
        if (camera.getInitializedAt() != null && !camera.getInitializedAt().toString().isBlank()) {
            throw new CameraAlreadyInitializedException("Camera already initialized");
        }

        try {
            camera.setInitializedAt(dateTimeFactory.now());
            cameraRepository.save(camera);
            log.info("Camera initialized with ID: {}", cameraId);
        } catch (Exception ex) {
            log.error("Exception occurred while initializing camera with ID: {}", cameraId, ex);
            throw new CameraNotInitializedException(String.format("Error occurred while initializing camera: %s", ex.getMessage()));
        }
    }

    public Camera getCameraById(UUID cameraId) {

        if (cameraId == null) {
            throw new IllegalArgumentException("Camera ID cannot be null");
        }

        return cameraRepository.findById(cameraId)
                .orElseThrow(() -> new CameraNotFoundException(String.format("Camera not found with id: %s", cameraId)));
    }

    @Override
    public void handleUploadImage(UUID cameraId, UUID imageId, byte[] imageData) {
        Camera camera = getCameraById(cameraId);
        validateCameraImage(camera);

        log.info("Received file:{}", imageId);
        try {
            if (camera.getImageId() != null) {
                throw new ImageAlreadyUploadedException(String.format("Camera already have image with id: %s", camera.getImageId()));
            }
            camera.setImageId(imageId);
            log.info("Uploading image with ID: {}", imageId);
            blobStorageService.uploadFile(blobStorageService.getContainerName(), imageId.toString(), imageData);
            cameraRepository.save(camera);
        } catch (ImageAlreadyUploadedException ex) {
            log.error("Exception occurred while uploading image");
            throw new ImageAlreadyUploadedException(String.format("Camera already have image with id: %s", camera.getImageId()));
        } catch (Exception ex) {
            log.error("Exception occurred while uploading image:{}:ex:{}", imageId, ex.getMessage());
            throw new ImageNotUploadedException(String.format("Error occurred while uploading image: %s", ex.getMessage()));
        }
    }

    @Override
    public byte[] handleDownloadImage(UUID cameraId) {
        Camera camera = getCameraById(cameraId);
        validateCameraImage(camera);

        try {
            if (camera.getImageId() == null) {
                throw new ImageNotFoundException(String.format("Image is not found by given cameraId: %s", cameraId));
            }

            log.info("Downloading image with ID: {}", camera.getImageId());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            blobStorageService.getBlob(outputStream, blobStorageService.getContainerName(), camera.getImageId().toString());

            return outputStream.toByteArray();
        } catch (ImageNotFoundException ex) {
            log.error("Image is not found by given cameraId: '{}'", cameraId);
            throw new ImageNotFoundException(String.format("Image is not found by given cameraId: %s", cameraId));
        } catch (Exception ex) {
            log.error("Exception occurred while downloading image, camera:{}:ex:{}", cameraId, ex.getMessage());
            throw new ImageNotDownloadedException(String.format("Error occurred while downloading image: %s", ex.getMessage()));
        }
    }

    @Override
    @Transactional
    public Camera handleAddLocation(UUID cameraId, LocationDto locationDto) {
        Camera camera = getCameraById(cameraId);

        try {
            Location location = Optional.ofNullable(camera.getLocation()).orElse(new Location());
            location.setLatitude(locationDto.getLatitude());
            location.setLongitude(locationDto.getLongitude());
            location.setAddress(locationDto.getAddress());

            location.setCamera(camera);
            camera.setLocation(location);

            cameraRepository.save(camera);
            log.info("Location added/updated successfully for Camera ID: {}", cameraId);
            return camera;
        } catch (Exception ex) {
            log.error("Exception occurred while adding location, camera:{}:ex:{}", cameraId, ex.getMessage());
            throw new LocationNotAddedException(String.format("Error occurred while adding location: %s", ex.getMessage()));
        }
    }

    /**
     * Validates if the camera has been onboarded and initialized
     *
     * @param camera the camera to check
     * @throws CameraNotFoundException       if the camera is not onboarded
     * @throws CameraNotInitializedException if the camera is not initialized
     */
    private void validateCameraImage(Camera camera) {
        if (camera.getOnboardedAt() == null || camera.getOnboardedAt().toString().isBlank()) {
            throw new CameraNotFoundException(String.format("Camera is not onboarded with id: %s", camera.getCamId()));
        } else if (camera.getInitializedAt() == null || camera.getInitializedAt().toString().isBlank()) {
            throw new CameraNotInitializedException(String.format("Camera is not initialized with id: %s", camera.getCamId()));
        }
    }
}
