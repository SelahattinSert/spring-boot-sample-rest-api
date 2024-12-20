package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.entity.Sensor;
import com.onboarding.camera.cameraonboarding.exception.SensorNotCreatedException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotUpdatedException;

import java.util.List;
import java.util.UUID;

public interface SensorService<T extends Sensor> {

    /**
     * this method is used for create sensor to specified camera
     *
     * @param cameraId camera id
     * @param sensor   sensor
     * @return Sensor created sensor
     * @throws IllegalArgumentException  if sensor type does not match
     * @throws SensorNotCreatedException if unexpected error occurs while creating sensor
     */
    T handleCreateSensor(UUID cameraId, T sensor);

    /**
     * this method is used for get all sensors with related camera by camera ID
     *
     * @param cameraId camera id
     * @return List<Sensor></Sensor> sensors with specified camera
     * @throws SensorNotFoundException if sensors not found
     */
    List<T> handleGetSensorsByCameraId(UUID cameraId);

    /**
     * this method is used for update sensor by sensor ID
     *
     * @param cameraId camera id
     * @param sensorId sensor id
     * @param sensor   sensor
     * @return Sensor updated sensor
     * @throws SensorNotUpdatedException if unexpected error occurs while updating sensor
     */
    T handleUpdateSensor(UUID cameraId, UUID sensorId, T sensor);

    /**
     * this method is used for get sensor by sensor ID
     *
     * @param sensorId sensor id
     * @return Sensor retrieved sensor
     * @throws SensorNotFoundException if sensor not found
     */
    T getSensorById(UUID sensorId);

    /**
     * this method is used for deleting sensor by sensor ID
     *
     * @param cameraId sensor id
     * @param sensorId sensor id
     * @throws SensorNotUpdatedException if unexpected error occurs while deleting sensor
     */
    void handleDeleteSensor(UUID cameraId, UUID sensorId);
}
