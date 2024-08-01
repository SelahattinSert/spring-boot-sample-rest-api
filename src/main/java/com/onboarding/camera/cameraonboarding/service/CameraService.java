package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import java.util.UUID;

public interface CameraService {

    /**
     * this method is used for saving camera_metadata
     *
     * @param camera Metadata
     * @return persistedCameraMetadata
     */

    Camera handleSaveCamera(Camera camera);

    /**
     * this method is used for initialize camera
     *
     * @param cameraId camera id
     */

    void handleInitializeCamera(UUID cameraId);

    /**
     * this method is used for find camera by id
     *
     * @param cameraId camera id
     * @return cameraWithFoundId
     */

    Camera findCameraById(UUID cameraId);
}
