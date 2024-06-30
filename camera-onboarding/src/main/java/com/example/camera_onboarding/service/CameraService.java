package com.example.camera_onboarding.service;

import com.example.camera_onboarding.entity.Camera;

import java.util.List;

public interface CameraService {

    List<Camera> getAllCameras();

    Camera getCamera(int cameraId);

    Camera saveCamera(Camera camera);

    void deleteCamera(int cameraId);
}
