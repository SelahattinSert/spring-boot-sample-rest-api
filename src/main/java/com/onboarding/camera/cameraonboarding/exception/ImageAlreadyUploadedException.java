package com.onboarding.camera.cameraonboarding.exception;

public class ImageAlreadyUploadedException extends RuntimeException {
    public ImageAlreadyUploadedException(final String message) {
        super(message);
    }
}
