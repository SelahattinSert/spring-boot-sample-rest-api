package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.entity.Camera;

public interface CameraService {

    /**
     * this method is used for saving camera_metadata
     *
     * @param camera Metadata
     * @return persistedCameraMetadata
     */

    Camera handleSaveCamera(Camera camera);
}
