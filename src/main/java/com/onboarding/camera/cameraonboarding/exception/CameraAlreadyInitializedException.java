package com.onboarding.camera.cameraonboarding.exception;

public class CameraAlreadyInitializedException extends RuntimeException{
    public CameraAlreadyInitializedException(String message) {
        super(message);
    }
}
