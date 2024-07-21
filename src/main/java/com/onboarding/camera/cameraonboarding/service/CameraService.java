package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import java.util.List;
import java.util.UUID;

public interface CameraService {

    List<Camera> getAllCameras();

    Camera getCameraById(UUID cameraId);

    Camera saveCamera(Camera camera);

    void deleteCameraById(UUID cameraId);
}
