package com.onboarding.camera.cameraonboarding.service.impl;

import com.onboarding.camera.cameraonboarding.exception.CameraNotFoundException;
import com.onboarding.camera.cameraonboarding.repository.CameraRepository;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.exception.CameraNotCreatedException;
import com.onboarding.camera.cameraonboarding.rest.CameraRestController;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import com.onboarding.camera.cameraonboarding.service.DateTimeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class CameraServiceImpl implements CameraService {

    private final CameraRepository cameraRepository;

    private static final Logger logger = LoggerFactory.getLogger(CameraRestController.class);

    private final DateTimeFactory dateTimeFactory;

    public CameraServiceImpl(CameraRepository cameraRepository, DateTimeFactory dateTimeFactory) {
        this.cameraRepository = cameraRepository;
        this.dateTimeFactory = dateTimeFactory;
    }

    @Override
    public Camera handleSaveCamera(Camera camera) {

        try {
            camera.setCreatedAt(dateTimeFactory.now());
            Camera savedCamera = cameraRepository.save(camera);
            logger.info("Saved camera with ID: {}", savedCamera.getCamId());
            return savedCamera;
        } catch (Exception ex) {
            logger.error("Exception occurred while saving camera: {}", ex.getMessage());
            throw new CameraNotCreatedException("Error occurred while saving camera: " + ex.getMessage());
        }
    }

    @Override
    public void handleInitializeCamera(UUID cameraId) {

        try {
            Camera camera = findCameraById(cameraId);
            camera.setInitializedAt(dateTimeFactory.now());
            cameraRepository.save(camera);
            logger.info("Initialized camera with ID: {}", cameraId);
        } catch (CameraNotFoundException ex) {
            logger.error("Camera not found with ID: {}", cameraId, ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Exception occurred while initializing camera with ID: {}", cameraId, ex);
            throw new RuntimeException("Error occurred while initializing camera: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Camera findCameraById(UUID cameraId) {

        if (cameraId == null) {
            throw new IllegalArgumentException("Camera ID cannot be null");
        }

        Optional<Camera> result = cameraRepository.findById(cameraId);

        Camera camera;

        if (result.isPresent()) {
            camera = result.get();
        } else {
            throw new CameraNotFoundException("Camera not found with id: " + cameraId);
        }

        return camera;
    }

}
