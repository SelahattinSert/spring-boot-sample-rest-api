package com.onboarding.camera.cameraonboarding.service.impl;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.entity.LightSensor;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import com.onboarding.camera.cameraonboarding.exception.CameraNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotCreatedException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotUpdatedException;
import com.onboarding.camera.cameraonboarding.repository.LightSensorRepository;
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
public class LightSensorService implements SensorService<LightSensor> {

    private final LightSensorRepository lightSensorRepository;

    private final CameraService cameraService;

    @Override
    @Transactional
    public LightSensor handleCreateSensor(UUID cameraId, LightSensor sensor) {
        try {
            sensor.setCamera(cameraService.getCameraById(cameraId));
            LightSensor createdSensor = lightSensorRepository.save(sensor);
            log.info("Creating sensor: {}", createdSensor);
            return createdSensor;
        } catch (CameraNotFoundException ex) {
            log.error("Camera not found, cameraId:{}", cameraId);
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to create sensor, camera:{}:ex:{}", cameraId, ex.getMessage());
            throw new SensorNotCreatedException(String.format("Failed to create sensor: %s", ex.getMessage()));
        }
    }

    @Override
    public List<LightSensor> handleGetSensorsByCameraId(UUID cameraId) {
        try {
            Camera camera = cameraService.getCameraById(cameraId);
            log.info("Getting sensors by camera id: {}", cameraId);
            return lightSensorRepository.findLightSensorByCamera(camera);
        } catch (CameraNotFoundException ex) {
            log.error("Camera not found, cameraId:{}", cameraId);
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while getting sensors, cameraId:{}", cameraId);
            throw new SensorNotFoundException(String.format("Error occurred while getting sensors: %s", ex.getMessage()));
        }
    }

    @Override
    @Transactional
    public LightSensor handleUpdateSensor(UUID cameraId, UUID sensorId, LightSensor sensor) {
        try {
            Camera camera = cameraService.getCameraById(cameraId);
            LightSensor existingSensor = camera.getSensors().stream()
                    .filter(s -> s.getId().equals(sensorId) && s.getSensorType().equals(SensorType.LIGHT))
                    .map(s -> (LightSensor) s)
                    .findFirst()
                    .orElseThrow(() -> new SensorNotFoundException(String.format("Sensor not found with id: %s", sensorId)));

            existingSensor.setName(sensor.getName());
            existingSensor.setVersion(sensor.getVersion());
            existingSensor.setData(sensor.getData());
            log.info("Updating sensor: {}", existingSensor);
            return lightSensorRepository.save(existingSensor);
        } catch (SensorNotFoundException ex) {
            log.error("Sensor not found while updating, sensorId: {}", sensorId);
            throw ex;
        } catch (CameraNotFoundException ex) {
            log.error("Camera not found, cameraId:{}", cameraId);
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while updating sensor, sensorId:{}", sensorId);
            throw new SensorNotUpdatedException(String.format("Error occurred while updating sensors: %s", ex.getMessage()));
        }
    }

    @Override
    public LightSensor getSensorById(UUID sensorId) {

        return lightSensorRepository.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException(String.format("Sensor not found with id: %s", sensorId)));
    }

    @Override
    public void handleDeleteSensor(UUID cameraId, UUID sensorId) {
        try {
            Camera camera = cameraService.getCameraById(cameraId);
            LightSensor sensor = camera.getSensors().stream()
                    .filter(s -> s.getId().equals(sensorId) && s.getSensorType().equals(SensorType.LIGHT))
                    .map(s -> (LightSensor) s)
                    .findFirst()
                    .orElseThrow(() -> new SensorNotFoundException(String.format("Sensor not found with id: %s", sensorId)));

            camera.getSensors().remove(sensor);
            lightSensorRepository.delete(sensor);
            log.info("Deleted sensor: {}", sensorId);
        } catch (CameraNotFoundException ex) {
            log.error("Camera not found, cameraId:{}", cameraId);
            throw ex;
        } catch (SensorNotFoundException ex) {
            log.error("Sensor not found while deleting, sensorId: {}", sensorId);
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while deleting sensor, sensorId:{}", sensorId);
            throw new SensorNotUpdatedException(String.format("Error occurred while deleting sensor: %s", ex.getMessage()));
        }
    }
}
