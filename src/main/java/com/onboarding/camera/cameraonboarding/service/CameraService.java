package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.dto.LocationDto;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.exception.CameraAlreadyInitializedException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotCreatedException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotInitializedException;
import com.onboarding.camera.cameraonboarding.exception.ImageNotDownloadedException;
import com.onboarding.camera.cameraonboarding.exception.ImageNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.ImageNotUploadedException;
import com.onboarding.camera.cameraonboarding.exception.LocationNotAddedException;

import java.util.UUID;

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
     * @throws CameraNotFoundException           if camera is not found with id
     * @throws CameraAlreadyInitializedException if camera is already initialized
     * @throws CameraNotInitializedException     if unexpected error occurred while initializing camera
     */

    void handleInitializeCamera(UUID cameraId);

    /**
     * this method is used for find camera by id
     *
     * @param cameraId camera id
     * @return cameraWithFoundId
     * @throws IllegalArgumentException if camera id is null
     * @throws CameraNotFoundException  if camera is not found with id
     */

    Camera getCameraById(UUID cameraId);

    /**
     * this method is used for uploading camera images to the azure blob storage
     *
     * @param cameraId  camera id
     * @param imageId   image id
     * @param imageData image data to be uploaded
     * @throws CameraNotFoundException       if camera is not onboarded
     * @throws CameraNotInitializedException if camera is not initialized
     * @throws ImageNotUploadedException     if unexpected error occurs while uploading image
     */

    void handleUploadImage(UUID cameraId, UUID imageId, byte[] imageData);

    /**
     * this method is used for downloading camera image from the azure blob storage
     *
     * @param cameraId camera id
     * @return byte[] image as bytes
     * @throws CameraNotFoundException       if camera is not onboarded
     * @throws CameraNotInitializedException if camera is not initialized
     * @throws ImageNotFoundException        if camera has no image
     * @throws ImageNotDownloadedException   if unexpected error occurs while downloading image
     */

    byte[] handleDownloadImage(UUID cameraId);

    /**
     * this method is used for add location information to camera
     *
     * @param cameraId    camera id
     * @param locationDto camera location
     * @return updated camera
     * @throws CameraNotFoundException   if camera is not found with id
     * @throws LocationNotAddedException if unexpected error occurs while adding location
     */

    Camera handleAddLocation(UUID cameraId, LocationDto locationDto);
}
