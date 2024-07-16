package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.dao.CameraRepository;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CameraServiceImpl implements CameraService {

    private CameraRepository cameraRepository;

    @Autowired
    public CameraServiceImpl(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }

    @Override
    public List<Camera> getAllCameras() {
        return cameraRepository.findAll();
    }

    @Override
    public Camera getCameraById(UUID cameraId) {

        Optional<Camera> result = cameraRepository.findById(cameraId);

        Camera camera = null;

        if (result.isPresent()) {
            camera = result.get();
        }
        else {
            throw new RuntimeException("Camera not found with id: " + cameraId);
        }

        return camera;
    }

    @Override
    public Camera saveCamera(Camera camera) {
        return cameraRepository.save(camera);
    }

    @Override
    public void deleteCameraById(UUID cameraId) {
        cameraRepository.deleteById(cameraId);
    }
}
