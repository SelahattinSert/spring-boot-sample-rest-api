package com.onboarding.camera.cameraonboarding.service.impl;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.entity.TemperatureSensor;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import com.onboarding.camera.cameraonboarding.exception.CameraNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotCreatedException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotUpdatedException;
import com.onboarding.camera.cameraonboarding.repository.TemperatureSensorRepository;
import com.onboarding.camera.cameraonboarding.service.CameraMetricService;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import com.onboarding.camera.cameraonboarding.service.SensorService;
import io.micrometer.core.annotation.Timed;
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

    private final CameraMetricService cameraMetricService;

    @Override
    @Transactional
    @Timed("sensor.create")
    public TemperatureSensor handleCreateSensor(UUID cameraId, TemperatureSensor sensor) {
        try {
            sensor.setCamera(cameraService.getCameraById(cameraId));
            TemperatureSensor createdSensor = temperatureSensorRepository.save(sensor);
            log.info("Creating sensor: {}", createdSensor);
            cameraMetricService.incrementSensorCreateSuccess(SensorType.TEMPERATURE.name());
            return createdSensor;
        } catch (CameraNotFoundException ex) {
            log.error("Camera not found, cameraId:{}", cameraId);
            cameraMetricService.incrementSensorCreateFailure(SensorType.TEMPERATURE.name());
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to create sensor, camera:{}:ex:{}", cameraId, ex.getMessage());
            cameraMetricService.incrementSensorCreateFailure(SensorType.TEMPERATURE.name());
            throw new SensorNotCreatedException(String.format("Failed to create sensor: %s", ex.getMessage()));
        }
    }

    @Override
    public List<TemperatureSensor> handleGetSensorsByCameraId(UUID cameraId) {
        try {
            Camera camera = cameraService.getCameraById(cameraId);
            log.info("Getting sensors by camera id: {}", cameraId);
            return temperatureSensorRepository.findTemperatureSensorByCamera(camera);
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
    @Timed("sensor.update")
    public TemperatureSensor handleUpdateSensor(UUID cameraId, UUID sensorId, TemperatureSensor sensor) {
        try {
            Camera camera = cameraService.getCameraById(cameraId);
            TemperatureSensor existingSensor = camera.getSensors().stream()
                    .filter(s -> s.getId().equals(sensorId) && s.getSensorType().equals(SensorType.TEMPERATURE))
                    .map(s -> (TemperatureSensor) s)
                    .findFirst()
                    .orElseThrow(() -> new SensorNotFoundException(String.format("Sensor not found with id: %s", sensorId)));

            existingSensor.setName(sensor.getName());
            existingSensor.setVersion(sensor.getVersion());
            existingSensor.setData(sensor.getData());
            log.info("Updating sensor: {}", existingSensor);
            cameraMetricService.incrementSensorUpdateSuccess(SensorType.TEMPERATURE.name());
            return temperatureSensorRepository.save(existingSensor);
        } catch (CameraNotFoundException ex) {
            log.error("Camera not found, cameraId:{}", cameraId);
            cameraMetricService.incrementSensorUpdateFailure(SensorType.TEMPERATURE.name());
            throw ex;
        } catch (SensorNotFoundException ex) {
            log.error("Sensor not found while updating, sensorId: {}", sensorId);
            cameraMetricService.incrementSensorUpdateFailure(SensorType.TEMPERATURE.name());
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while updating sensor, sensorId:{}", sensorId);
            cameraMetricService.incrementSensorUpdateFailure(SensorType.TEMPERATURE.name());
            throw new SensorNotUpdatedException(String.format("Error occurred while updating sensors: %s", ex.getMessage()));
        }
    }

    @Override
    public TemperatureSensor getSensorById(UUID sensorId) {

        return temperatureSensorRepository.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException(String.format("Sensor not found with id: %s", sensorId)));
    }

    @Override
    @Timed("sensor.delete")
    public void handleDeleteSensor(UUID cameraId, UUID sensorId) {
        try {
            Camera camera = cameraService.getCameraById(cameraId);
            TemperatureSensor sensor = camera.getSensors().stream()
                    .filter(s -> s.getId().equals(sensorId) && s.getSensorType().equals(SensorType.TEMPERATURE))
                    .map(s -> (TemperatureSensor) s)
                    .findFirst()
                    .orElseThrow(() -> new SensorNotFoundException(String.format("Sensor not found with id: %s", sensorId)));

            camera.getSensors().remove(sensor);
            temperatureSensorRepository.delete(sensor);
            log.info("Deleted sensor: {}", sensorId);
            cameraMetricService.incrementSensorDeleteSuccess(SensorType.TEMPERATURE.name());
        } catch (CameraNotFoundException ex) {
            log.error("Camera not found, cameraId:{}", cameraId);
            cameraMetricService.incrementSensorDeleteFailure(SensorType.TEMPERATURE.name());
            throw ex;
        } catch (SensorNotFoundException ex) {
            log.error("Sensor not found while deleting, sensorId: {}", sensorId);
            cameraMetricService.incrementSensorDeleteFailure(SensorType.TEMPERATURE.name());
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while deleting sensor, sensorId:{}", sensorId);
            cameraMetricService.incrementSensorDeleteFailure(SensorType.TEMPERATURE.name());
            throw new SensorNotUpdatedException(String.format("Error occurred while deleting sensor: %s", ex.getMessage()));
        }
    }
}
