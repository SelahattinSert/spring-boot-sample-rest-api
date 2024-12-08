package com.onboarding.camera.cameraonboarding.service.impl;

import com.onboarding.camera.cameraonboarding.entity.MotionSensor;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import com.onboarding.camera.cameraonboarding.exception.SensorNotCreatedException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotUpdatedException;
import com.onboarding.camera.cameraonboarding.repository.MotionSensorRepository;
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
public class MotionSensorService implements SensorService<MotionSensor> {

    private final MotionSensorRepository motionSensorRepository;

    private final CameraService cameraService;

    @Override
    @Transactional
    public MotionSensor handleCreateSensor(UUID cameraId, MotionSensor sensor) {
        try {
            sensor.setCamera(cameraService.getCameraById(cameraId));
            sensor.setSensorType(SensorType.MOTION);
            log.info("Creating sensor: {}", sensor);
            return motionSensorRepository.save(sensor);
        } catch (SensorNotCreatedException ex) {
            log.error("Failed to create sensor, camera:{}:ex:{}", cameraId, ex.getMessage());
            throw new SensorNotCreatedException(String.format("Failed to create sensor: %s", ex.getMessage()));
        }
    }

    @Override
    public List<MotionSensor> handleGetSensorsByCameraId(UUID cameraId) {
        try {
            log.info("Getting sensors by camera id: {}", cameraId);
            return motionSensorRepository.findByCameraIdAndSensorType(cameraId, SensorType.MOTION);
        } catch (SensorNotFoundException ex) {
            log.error("Exception occurred while getting sensors, cameraId:{}", cameraId);
            throw new SensorNotFoundException(String.format("Error occurred while getting sensors: %s", ex.getMessage()));
        }
    }

    @Override
    @Transactional
    public MotionSensor handleUpdateSensor(UUID sensorId, MotionSensor sensor) {
        try {
            MotionSensor existingSensor = getSensorById(sensorId);
            existingSensor.setName(sensor.getName());
            existingSensor.setVersion(sensor.getVersion());
            existingSensor.setData(sensor.getData());
            log.info("Updating sensor: {}", existingSensor);
            return motionSensorRepository.save(existingSensor);
        } catch (SensorNotUpdatedException ex) {
            log.error("Exception occurred while updating sensor, sensorId:{}", sensorId);
            throw new SensorNotUpdatedException(String.format("Error occurred while updating sensors: %s", ex.getMessage()));
        }
    }

    @Override
    public MotionSensor getSensorById(UUID sensorId) {
        if (sensorId == null) {
            throw new IllegalArgumentException("Sensor ID cannot be null");
        }

        return motionSensorRepository.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException(String.format("Sensor not found with id: %s", sensorId)));
    }

    @Override
    public void handleDeleteSensor(UUID sensorId) {
        try {
            MotionSensor deletedSensor = getSensorById(sensorId);
            log.info("Deleting sensor: {}", deletedSensor);
            motionSensorRepository.deleteById(sensorId);
        } catch (SensorNotUpdatedException ex) {
            log.error("Exception occurred while deleting sensor, sensorId:{}", sensorId);
            throw new SensorNotUpdatedException(String.format("Error occurred while deleting sensor: %s", ex.getMessage()));
        }
    }
}