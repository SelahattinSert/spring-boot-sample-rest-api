package com.onboarding.camera.cameraonboarding.service.impl;

import com.onboarding.camera.cameraonboarding.dao.CameraRepository;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.exception.CameraNotCreatedException;
import com.onboarding.camera.cameraonboarding.rest.CameraRestController;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CameraServiceImpl implements CameraService {

    private final CameraRepository cameraRepository;

    private static final Logger logger = LoggerFactory.getLogger(CameraRestController.class);

    private final DateTimeFactoryImpl dateTimeFactoryImpl;

    public CameraServiceImpl(CameraRepository cameraRepository, DateTimeFactoryImpl dateTimeFactoryImpl) {
        this.cameraRepository = cameraRepository;
        this.dateTimeFactoryImpl = dateTimeFactoryImpl;
    }

    @Override
    public Camera handleSaveCamera(Camera camera) {

        try {
            camera.setCreatedAt(dateTimeFactoryImpl.now());
            Camera savedCamera = cameraRepository.save(camera);
            logger.info("Saved camera with ID: {}", savedCamera.getCamId());
            return savedCamera;
        } catch (Exception ex) {
            logger.error("Exception occurred while saving camera: {}", ex.getMessage());
            throw new CameraNotCreatedException("Error occurred while saving camera: " + ex.getMessage());
        }
    }

}
