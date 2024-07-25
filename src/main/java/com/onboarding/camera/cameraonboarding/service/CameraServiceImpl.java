package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.dao.CameraRepository;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.exception.CameraNotFoundException;
import com.onboarding.camera.cameraonboarding.rest.CameraRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CameraServiceImpl implements CameraService {

    private final CameraRepository cameraRepository;

    private static final Logger logger = LoggerFactory.getLogger(CameraRestController.class);

    @Autowired
    public CameraServiceImpl(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }

    @Override
    public Camera handleSaveCamera(Camera camera) {

        try {
            Camera savedCamera = cameraRepository.save(camera);
            logger.info("Saved camera with ID: {}", savedCamera.getCamId());
            return savedCamera;
        } catch (Exception ex) {
            logger.error("Exception occurred while saving camera: {}", ex.getMessage());
            throw new CameraNotFoundException("Error occurred while saving camera: " + ex.getMessage());
        }
    }

}
