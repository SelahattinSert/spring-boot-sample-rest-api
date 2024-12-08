package com.onboarding.camera.cameraonboarding.converter;

import com.onboarding.camera.cameraonboarding.dto.SensorDto;
import com.onboarding.camera.cameraonboarding.dto.SensorResponse;
import com.onboarding.camera.cameraonboarding.entity.LightSensor;
import com.onboarding.camera.cameraonboarding.entity.MotionSensor;
import com.onboarding.camera.cameraonboarding.entity.Sensor;
import com.onboarding.camera.cameraonboarding.entity.TemperatureSensor;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import com.onboarding.camera.cameraonboarding.exception.SensorMismatchException;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SensorDtoConverter {

    public MotionSensor toMotionEntity(SensorDto sensorDto) {
        return Stream.of(sensorDto)
                .filter(dto -> dto.getSensorType() == SensorType.MOTION)
                .map(dto -> {
                    MotionSensor sensorMetadata = new MotionSensor();
                    sensorMetadata.setName(dto.getName());
                    sensorMetadata.setVersion(dto.getVersion());
                    sensorMetadata.setSensorType(dto.getSensorType());
                    sensorMetadata.setData(dto.getData());
                    return sensorMetadata;
                })
                .findFirst()
                .orElseThrow(() -> new SensorMismatchException("Invalid sensor type for MotionSensor"));
    }

    public LightSensor toLightEntity(SensorDto sensorDto) {
        return Stream.of(sensorDto)
                .filter(dto -> dto.getSensorType() == SensorType.LIGHT)
                .map(dto -> {
                    LightSensor sensorMetadata = new LightSensor();
                    sensorMetadata.setName(dto.getName());
                    sensorMetadata.setVersion(dto.getVersion());
                    sensorMetadata.setSensorType(dto.getSensorType());
                    sensorMetadata.setData(dto.getData());
                    return sensorMetadata;
                })
                .findFirst()
                .orElseThrow(() -> new SensorMismatchException("Invalid sensor type for LightSensor"));
    }

    public TemperatureSensor toTemperatureEntity(SensorDto sensorDto) {
        return Stream.of(sensorDto)
                .filter(dto -> dto.getSensorType() == SensorType.TEMPERATURE)
                .map(dto -> {
                    TemperatureSensor sensorMetadata = new TemperatureSensor();
                    sensorMetadata.setName(dto.getName());
                    sensorMetadata.setVersion(dto.getVersion());
                    sensorMetadata.setSensorType(dto.getSensorType());
                    sensorMetadata.setData(dto.getData());
                    return sensorMetadata;
                })
                .findFirst()
                .orElseThrow(() -> new SensorMismatchException("Invalid sensor type for TemperatureSensor"));
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
}
