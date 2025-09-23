package com.onboarding.camera.cameraonboarding.service.impl;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.entity.MotionSensor;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import com.onboarding.camera.cameraonboarding.exception.CameraNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotCreatedException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotUpdatedException;
import com.onboarding.camera.cameraonboarding.repository.MotionSensorRepository;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import com.onboarding.camera.cameraonboarding.service.MetricsService;
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
public class MotionSensorService implements SensorService<MotionSensor> {

    private final MotionSensorRepository motionSensorRepository;

    private final CameraService cameraService;

    private final MetricsService metricsService;

    @Override
    @Transactional
    @Timed("sensor.create")
    public MotionSensor handleCreateSensor(UUID cameraId, MotionSensor sensor) {
        try {
            sensor.setCamera(cameraService.getCameraById(cameraId));
            MotionSensor createdSensor = motionSensorRepository.save(sensor);
            log.info("Creating sensor: {}", createdSensor);
            metricsService.incrementSensorCreateSuccess(SensorType.MOTION.name());
            return createdSensor;
        } catch (CameraNotFoundException ex) {
            log.error("Camera not found, cameraId:{}", cameraId);
            metricsService.incrementSensorCreateFailure(SensorType.MOTION.name());
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to create sensor, camera:{}:ex:{}", cameraId, ex.getMessage());
            metricsService.incrementSensorCreateFailure(SensorType.MOTION.name());
            throw new SensorNotCreatedException(String.format("Failed to create sensor: %s", ex.getMessage()));
        }
    }

    @Override
    public List<MotionSensor> handleGetSensorsByCameraId(UUID cameraId) {
        try {
            Camera camera = cameraService.getCameraById(cameraId);
            log.info("Getting sensors by camera id: {}", cameraId);
            return motionSensorRepository.findMotionSensorByCamera(camera);
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
    public MotionSensor handleUpdateSensor(UUID cameraId, UUID sensorId, MotionSensor sensor) {
        try {
            Camera camera = cameraService.getCameraById(cameraId);
            MotionSensor existingSensor = camera.getSensors().stream()
                    .filter(s -> s.getId().equals(sensorId) && s.getSensorType().equals(SensorType.MOTION))
                    .map(s -> (MotionSensor) s)
                    .findFirst()
                    .orElseThrow(() -> new SensorNotFoundException(String.format("Sensor not found with id: %s", sensorId)));

            existingSensor.setName(sensor.getName());
            existingSensor.setVersion(sensor.getVersion());
            existingSensor.setData(sensor.getData());
            log.info("Updating sensor: {}", existingSensor);
            metricsService.incrementSensorUpdateSuccess(SensorType.MOTION.name());
            return motionSensorRepository.save(existingSensor);
        } catch (CameraNotFoundException ex) {
            log.error("Camera not found, cameraId:{}", cameraId);
            metricsService.incrementSensorUpdateFailure(SensorType.MOTION.name());
            throw ex;
        } catch (SensorNotFoundException ex) {
            log.error("Sensor not found while updating, sensorId: {}", sensorId);
            metricsService.incrementSensorUpdateFailure(SensorType.MOTION.name());
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while updating sensor, sensorId:{}", sensorId);
            metricsService.incrementSensorUpdateFailure(SensorType.MOTION.name());
            throw new SensorNotUpdatedException(String.format("Error occurred while updating sensors: %s", ex.getMessage()));
        }
    }

    @Override
    public MotionSensor getSensorById(UUID sensorId) {

        return motionSensorRepository.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException(String.format("Sensor not found with id: %s", sensorId)));
    }

    @Override
    @Timed("sensor.delete")
    public void handleDeleteSensor(UUID cameraId, UUID sensorId) {
        try {
            Camera camera = cameraService.getCameraById(cameraId);
            MotionSensor sensor = camera.getSensors().stream()
                    .filter(s -> s.getId().equals(sensorId) && s.getSensorType().equals(SensorType.MOTION))
                    .map(s -> (MotionSensor) s)
                    .findFirst()
                    .orElseThrow(() -> new SensorNotFoundException(String.format("Sensor not found with id: %s", sensorId)));

            camera.getSensors().remove(sensor);
            motionSensorRepository.delete(sensor);
            log.info("Deleted sensor: {}", sensorId);
            metricsService.incrementSensorDeleteSuccess(SensorType.MOTION.name());
        } catch (CameraNotFoundException ex) {
            log.error("Camera not found, cameraId:{}", cameraId);
            metricsService.incrementSensorDeleteFailure(SensorType.MOTION.name());
            throw ex;
        } catch (SensorNotFoundException ex) {
            log.error("Sensor not found while deleting, sensorId: {}", sensorId);
            metricsService.incrementSensorDeleteFailure(SensorType.MOTION.name());
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while deleting sensor, sensorId:{}", sensorId);
            metricsService.incrementSensorDeleteFailure(SensorType.MOTION.name());
            throw new SensorNotUpdatedException(String.format("Error occurred while deleting sensor: %s", ex.getMessage()));
        }
    }
}