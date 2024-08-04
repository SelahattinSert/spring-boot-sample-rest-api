package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import java.util.UUID;
import com.onboarding.camera.cameraonboarding.exception.CameraNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotCreatedException;

public interface CameraService {

    /**
     * this method is used for saving camera_metadata
     *
     * @param camera Metadata
     * @return persistedCameraMetadata
     * @throws CameraNotCreatedException if unexpected error occurs while saving camera
     */

    Camera handleSaveCamera(Camera camera);

    /**
     * this method is used for initialize camera
     *
     * @param cameraId camera id
     * @throws CameraNotFoundException if camera is not found with id
     * @throws RuntimeException if unexpected error occurred while initializing camera
     */

    void handleInitializeCamera(UUID cameraId);

    /**
     * this method is used for find camera by id
     *
     * @param cameraId camera id
     * @return cameraWithFoundId
     * @throws IllegalArgumentException if camera id is null
     * @throws CameraNotFoundException if camera is not found with id
     */

    Camera getCameraById(UUID cameraId);
}
