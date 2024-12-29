package com.onboarding.camera.cameraonboarding.converter;

import com.onboarding.camera.cameraonboarding.dto.SensorDto;
import com.onboarding.camera.cameraonboarding.dto.SensorResponse;
import com.onboarding.camera.cameraonboarding.entity.LightSensor;
import com.onboarding.camera.cameraonboarding.entity.MotionSensor;
import com.onboarding.camera.cameraonboarding.entity.Sensor;
import com.onboarding.camera.cameraonboarding.entity.TemperatureSensor;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import com.onboarding.camera.cameraonboarding.exception.SensorMismatchException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotCreatedException;
import org.springframework.stereotype.Component;

@Component
public class SensorDtoConverter {

    public MotionSensor toMotionEntity(SensorDto sensorDto) {

        validateSensorDto(sensorDto);
        if (sensorDto.getSensorType() != SensorType.MOTION) {
            throw new SensorMismatchException("Invalid sensor type for MotionSensor");
        }

        MotionSensor sensorMetadata = new MotionSensor();
        sensorMetadata.setName(sensorDto.getName());
        sensorMetadata.setVersion(sensorDto.getVersion());
        sensorMetadata.setSensorType(sensorDto.getSensorType());
        sensorMetadata.setData(sensorDto.getData());
        return sensorMetadata;
    }

    public LightSensor toLightEntity(SensorDto sensorDto) {

        validateSensorDto(sensorDto);
        if (sensorDto.getSensorType() != SensorType.LIGHT) {
            throw new SensorMismatchException("Invalid sensor type for LightSensor");
        }

        LightSensor sensorMetadata = new LightSensor();
        sensorMetadata.setName(sensorDto.getName());
        sensorMetadata.setVersion(sensorDto.getVersion());
        sensorMetadata.setSensorType(sensorDto.getSensorType());
        sensorMetadata.setData(sensorDto.getData());
        return sensorMetadata;
    }

    public TemperatureSensor toTemperatureEntity(SensorDto sensorDto) {

        validateSensorDto(sensorDto);
        if (sensorDto.getSensorType() != SensorType.TEMPERATURE) {
            throw new SensorMismatchException("Invalid sensor type for TemperatureSensor");
        }

        TemperatureSensor sensorMetadata = new TemperatureSensor();
        sensorMetadata.setName(sensorDto.getName());
        sensorMetadata.setVersion(sensorDto.getVersion());
        sensorMetadata.setSensorType(sensorDto.getSensorType());
        sensorMetadata.setData(sensorDto.getData());
        return sensorMetadata;
    }

    public SensorResponse toSensorResponse(Sensor sensor) {
        SensorResponse response = new SensorResponse();
        response.setId(sensor.getId());
        response.setName(sensor.getName());
        response.setVersion(sensor.getVersion());
        response.setSensorType(sensor.getSensorType());
        response.setData(sensor.getData());
        return response;
    }

    /**
     * Validates if the camera has been onboarded and initialized
     *
     * @param sensorDto the sensor dto to check
     * @throws SensorNotCreatedException if the sensor name, version or type is null
     */
    private void validateSensorDto(SensorDto sensorDto) {
        if (sensorDto.getName() == null || sensorDto.getName().isBlank()) {
            throw new SensorNotCreatedException("Sensor name cannot be null");
        }
        if (sensorDto.getVersion() == null || sensorDto.getVersion().isBlank()) {
            throw new SensorNotCreatedException("Sensor version cannot be null");
        }
        if (sensorDto.getSensorType() == null || sensorDto.getSensorType().toString().isBlank()) {
            throw new SensorNotCreatedException("Sensor type cannot be null");
        }
    }
}
