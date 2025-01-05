package com.onboarding.camera.cameraonboarding.enums;

public enum SensorType {
    /**
     * MOTION: Used to detect movement in the vicinity of the camera.
     * This sensor is typically utilized in security systems to trigger alerts
     * or initiate recordings when motion is detected.
     */
    MOTION,

    /**
     * TEMPERATURE: Used to monitor the environmental temperature around the camera.
     * This sensor can be used in smart home systems, industrial setups, or outdoor
     * environments where temperature variations need to be tracked for safety or efficiency.
     */
    TEMPERATURE,

    /**
     * LIGHT: Used to measure the intensity of light around the camera.
     * This sensor can assist in adjusting the camera's exposure settings
     * or determining the need for additional lighting in low-light conditions.
     */
    LIGHT
}
