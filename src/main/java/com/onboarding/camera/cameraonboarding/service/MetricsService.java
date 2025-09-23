package com.onboarding.camera.cameraonboarding.service;

public interface MetricsService {

    /**
     * Increments the counter for successful camera onboarding.
     */
    void incrementCameraOnboardingSuccess();

    /**
     * Increments the counter for failed camera onboarding.
     */
    void incrementCameraOnboardingFailure();

    /**
     * Increments the counter for successful camera initialization.
     */
    void incrementCameraInitializationSuccess();

    /**
     * Increments the counter for failed camera initialization.
     */
    void incrementCameraInitializationFailure();

    /**
     * Increments the counter for successful image uploads.
     */
    void incrementImageUploadSuccess();

    /**
     * Increments the counter for failed image uploads.
     */
    void incrementImageUploadFailure();

    /**
     * Increments the counter for successful image downloads.
     */
    void incrementImageDownloadSuccess();

    /**
     * Increments the counter for failed image downloads.
     */
    void incrementImageDownloadFailure();

    /**
     * Increments the counter for successfully adding a location.
     */
    void incrementLocationAddSuccess();

    /**
     * Increments the counter for failed attempts at adding a location.
     */
    void incrementLocationAddFailure();

    /**
     * Increments the counter for successful sensor creation.
     *
     * @param sensorType the type of the sensor
     */
    void incrementSensorCreateSuccess(String sensorType);

    /**
     * Increments the counter for failed sensor creation.
     *
     * @param sensorType the type of the sensor
     */
    void incrementSensorCreateFailure(String sensorType);

    /**
     * Increments the counter for successful sensor updates.
     *
     * @param sensorType the type of the sensor
     */
    void incrementSensorUpdateSuccess(String sensorType);

    /**
     * Increments the counter for failed sensor updates.
     *
     * @param sensorType the type of the sensor
     */
    void incrementSensorUpdateFailure(String sensorType);

    /**
     * Increments the counter for successful sensor deletions.
     *
     * @param sensorType the type of the sensor
     */
    void incrementSensorDeleteSuccess(String sensorType);

    /**
     * Increments the counter for failed sensor deletions.
     *
     * @param sensorType the type of the sensor
     */
    void incrementSensorDeleteFailure(String sensorType);
}
