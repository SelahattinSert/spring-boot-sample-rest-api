package com.onboarding.camera.cameraonboarding.service.impl;

import com.onboarding.camera.cameraonboarding.exception.CameraAlreadyInitializedException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotInitializedException;
import com.onboarding.camera.cameraonboarding.repository.CameraRepository;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.exception.CameraNotCreatedException;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import com.onboarding.camera.cameraonboarding.util.DateTimeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
public class CameraServiceImpl implements CameraService {

    private final CameraRepository cameraRepository;

    private final DateTimeFactory dateTimeFactory;

    public CameraServiceImpl(CameraRepository cameraRepository, DateTimeFactory dateTimeFactory) {
        this.cameraRepository = cameraRepository;
        this.dateTimeFactory = dateTimeFactory;
    }

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
            throw new CameraNotCreatedException("Error occurred while saving camera: " + ex.getMessage());
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
            throw new CameraNotInitializedException("Error occurred while initializing camera: " + ex.getMessage());
        }
    }
    
    public Camera getCameraById(UUID cameraId) {

        if (cameraId == null) {
            throw new IllegalArgumentException("Camera ID cannot be null");
        }

        return cameraRepository.findById(cameraId)
                .orElseThrow(() -> new CameraNotFoundException("Camera not found with id: " + cameraId));
    }
}
