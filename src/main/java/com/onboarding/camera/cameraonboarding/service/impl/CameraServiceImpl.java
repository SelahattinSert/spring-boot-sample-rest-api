package com.onboarding.camera.cameraonboarding.service.impl;

import com.onboarding.camera.cameraonboarding.dto.UploadImageDto;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.exception.CameraAlreadyInitializedException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotCreatedException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotInitializedException;
import com.onboarding.camera.cameraonboarding.exception.ImageAlreadyUploadedException;
import com.onboarding.camera.cameraonboarding.exception.ImageNotUploadedException;
import com.onboarding.camera.cameraonboarding.repository.CameraRepository;
import com.onboarding.camera.cameraonboarding.service.BlobStorageService;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import com.onboarding.camera.cameraonboarding.util.DateTimeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
public class CameraServiceImpl implements CameraService {

    private final CameraRepository cameraRepository;

    private final BlobStorageService blobStorageService;

    private final DateTimeFactory dateTimeFactory;

    public CameraServiceImpl(final CameraRepository cameraRepository, final DateTimeFactory dateTimeFactory, final BlobStorageService blobStorageService) {
        this.cameraRepository = cameraRepository;
        this.dateTimeFactory = dateTimeFactory;
        this.blobStorageService = blobStorageService;
    }

    @Override
    public Camera handleSaveCamera(final Camera camera) {

        try {
            camera.setCreatedAt(dateTimeFactory.now());
            camera.setOnboardedAt(dateTimeFactory.now());
            final Camera savedCamera = cameraRepository.save(camera);
            log.info("Camera saved with ID: {}", savedCamera.getCamId());
            return savedCamera;
        } catch (final Exception ex) {
            log.error("Exception occurred while saving camera: {}", ex.getMessage());
            throw new CameraNotCreatedException("Error occurred while saving camera: " + ex.getMessage());
        }
    }

    @Override
    public void handleInitializeCamera(final UUID cameraId) {
        final Camera camera = getCameraById(cameraId);
        if (camera.getInitializedAt() != null && !camera.getInitializedAt().toString().isBlank()) {
            throw new CameraAlreadyInitializedException("Camera already initialized");
        }

        try {
            camera.setInitializedAt(dateTimeFactory.now());
            cameraRepository.save(camera);
            log.info("Camera initialized with ID: {}", cameraId);
        } catch (final Exception ex) {
            log.error("Exception occurred while initializing camera with ID: {}", cameraId, ex);
            throw new CameraNotInitializedException("Error occurred while initializing camera: " + ex.getMessage());
        }
    }

    public Camera getCameraById(final UUID cameraId) {

        if (cameraId == null) {
            throw new IllegalArgumentException("Camera ID cannot be null");
        }

        return cameraRepository.findById(cameraId)
                .orElseThrow(() -> new CameraNotFoundException("Camera not found with id: " + cameraId));
    }

    @Override
    public void handleUploadImage(final UUID cameraId, final UploadImageDto uploadImageDto) {
        final Camera camera = getCameraById(cameraId);
        if (camera.getOnboardedAt() == null || camera.getOnboardedAt().toString().isBlank()) {
            throw new CameraNotFoundException("Camera is not onboarded with id: " + cameraId);
        } else if (camera.getInitializedAt() == null || camera.getInitializedAt().toString().isBlank()) {
            throw new CameraNotInitializedException("Camera is not initialized with id: " + cameraId);
        }
        final MultipartFile file = uploadImageDto.getFile();
        final UUID imageId = uploadImageDto.getImageId();
        log.info("Received file:{}", file.getOriginalFilename());
        try {
            if (camera.getImageId() != null) {
                throw new ImageAlreadyUploadedException("Camera already have image with id: " + camera.getImageId());
            }
            camera.setImageId(imageId);
            cameraRepository.save(camera);
            log.info("Uploading image with ID: {}", imageId);
            blobStorageService.uploadFile(blobStorageService.getContainerName(), imageId.toString(), file.getBytes());
        } catch (final ImageAlreadyUploadedException ex) {
            log.error("Exception occurred while uploading image");
            throw new ImageAlreadyUploadedException("Camera already have image with id: " + camera.getImageId());
        } catch (final Exception ex) {
            log.error("Exception occurred while uploading image:{}:ex:{}", uploadImageDto.getFile().getOriginalFilename(), ex.getMessage());
            throw new ImageNotUploadedException("Error occurred while uploading image: " + ex.getMessage());
        }
    }
}
