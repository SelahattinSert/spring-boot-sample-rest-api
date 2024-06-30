package com.example.camera_onboarding.service;

import com.example.camera_onboarding.dao.CameraRepository;
import com.example.camera_onboarding.entity.Camera;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CameraServiceImpl implements CameraService {

    private CameraRepository cameraRepository;

    private static final Logger logger = LogManager.getLogger(CameraServiceImpl.class);

    @Autowired
    public CameraServiceImpl(CameraRepository cameraRepository) {

        this.cameraRepository = cameraRepository;
    }

    @Override
    public List<Camera> getAllCameras() {

        logger.info("Fetching all cameras");
        List<Camera> cameras = cameraRepository.findAll();
        logger.debug("Fetched cameras: {}", cameras);
        return cameras;
    }

    @Override
    public Camera getCamera(int cameraId) {

        logger.info("Fetching camera with ID: {}", cameraId);
        Optional<Camera> result = cameraRepository.findById(cameraId);

        if (!result.isPresent()) {
            logger.error("Camera not found with ID: {}", cameraId);
            throw new RuntimeException("Camera not found with id: " + cameraId);
        }

        Camera camera = result.get();
        logger.debug("Camera found: {}", camera);
        return camera;
    }

    @Transactional
    @Override
    public Camera saveCamera(Camera camera) {

        logger.info("Saving camera: {}", camera);
        Camera savedCamera = cameraRepository.save(camera);
        logger.debug("Camera saved: {}", savedCamera);

        return savedCamera;
    }

    @Override
    public void deleteCamera(int cameraId) {

        logger.info("Deleting camera with ID: {}", cameraId);
        cameraRepository.deleteById(cameraId);
        logger.debug("Camera deleted with ID: {}", cameraId);
    }
}
