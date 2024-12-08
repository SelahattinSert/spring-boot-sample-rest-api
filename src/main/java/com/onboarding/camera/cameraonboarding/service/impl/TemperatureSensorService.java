package com.onboarding.camera.cameraonboarding.service.impl;

import com.onboarding.camera.cameraonboarding.entity.TemperatureSensor;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import com.onboarding.camera.cameraonboarding.exception.SensorNotCreatedException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotUpdatedException;
import com.onboarding.camera.cameraonboarding.repository.TemperatureSensorRepository;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import com.onboarding.camera.cameraonboarding.service.SensorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TemperatureSensorService implements SensorService<TemperatureSensor> {

    private final TemperatureSensorRepository temperatureSensorRepository;

    private final CameraService cameraService;

    @Override
    @Transactional
    public TemperatureSensor handleCreateSensor(UUID cameraId, TemperatureSensor sensor) {
        try {
            sensor.setCamera(cameraService.getCameraById(cameraId));
            sensor.setSensorType(SensorType.TEMPERATURE);
            log.info("Creating sensor: {}", sensor);
            return temperatureSensorRepository.save(sensor);
        } catch (SensorNotCreatedException ex) {
            log.error("Failed to create sensor, camera:{}:ex:{}", cameraId, ex.getMessage());
            throw new SensorNotCreatedException(String.format("Failed to create sensor: %s", ex.getMessage()));
        }
    }

    @Override
    public List<TemperatureSensor> handleGetSensorsByCameraId(UUID cameraId) {
        try {
            log.info("Getting sensors by camera id: {}", cameraId);
            return temperatureSensorRepository.findByCameraIdAndSensorType(cameraId, SensorType.TEMPERATURE);
        } catch (SensorNotFoundException ex) {
            log.error("Exception occurred while getting sensors, cameraId:{}", cameraId);
            throw new SensorNotFoundException(String.format("Error occurred while getting sensors: %s", ex.getMessage()));
        }
    }

    @Override
    @Transactional
    public TemperatureSensor handleUpdateSensor(UUID sensorId, TemperatureSensor sensor) {
        try {
            TemperatureSensor existingSensor = getSensorById(sensorId);
            existingSensor.setName(sensor.getName());
            existingSensor.setVersion(sensor.getVersion());
            existingSensor.setData(sensor.getData());
            log.info("Updating sensor: {}", existingSensor);
            return temperatureSensorRepository.save(existingSensor);
        } catch (SensorNotUpdatedException ex) {
            log.error("Exception occurred while updating sensor, sensorId:{}", sensorId);
            throw new SensorNotUpdatedException(String.format("Error occurred while updating sensors: %s", ex.getMessage()));
        }
    }

    @Override
    public TemperatureSensor getSensorById(UUID sensorId) {
        if (sensorId == null) {
            throw new IllegalArgumentException("Sensor ID cannot be null");
        }

        return temperatureSensorRepository.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException(String.format("Sensor not found with id: %s", sensorId)));
    }

    @Override
    public void handleDeleteSensor(UUID sensorId) {
        try {
            TemperatureSensor deletedSensor = getSensorById(sensorId);
            log.info("Deleting sensor: {}", deletedSensor);
            temperatureSensorRepository.deleteById(sensorId);
        } catch (SensorNotUpdatedException ex) {
            log.error("Exception occurred while deleting sensor, sensorId:{}", sensorId);
            throw new SensorNotUpdatedException(String.format("Error occurred while deleting sensor: %s", ex.getMessage()));
        }
    }
}
