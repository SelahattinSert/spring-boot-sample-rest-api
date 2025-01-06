package com.onboarding.camera.cameraonboarding.controller;

import com.onboarding.camera.cameraonboarding.converter.SensorDtoConverter;
import com.onboarding.camera.cameraonboarding.dto.SensorDto;
import com.onboarding.camera.cameraonboarding.dto.SensorResponse;
import com.onboarding.camera.cameraonboarding.entity.TemperatureSensor;
import com.onboarding.camera.cameraonboarding.service.impl.TemperatureSensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.version}/camera/{cameraId}/sensor/temperature")
@RequiredArgsConstructor
@Tag(name = "Temperature Sensor Controller")
public class TemperatureSensorController {

    private final TemperatureSensorService temperatureSensorService;

    private final SensorDtoConverter sensorDtoConverter;

    @PostMapping
    @Operation(
            description = "Add temperature sensor to the related camera",
            summary = "This is an endpoint for Add temperature sensor"
    )
    public ResponseEntity<SensorResponse> addTemperatureSensor(
            @PathVariable UUID cameraId,
            @Valid @RequestBody SensorDto sensorDto) {

        TemperatureSensor sensorMetadata = sensorDtoConverter.toTemperatureEntity(sensorDto);
        TemperatureSensor savedSensor = temperatureSensorService.handleCreateSensor(cameraId, sensorMetadata);
        SensorResponse sensorResponse = sensorDtoConverter.toSensorResponse(savedSensor);
        return new ResponseEntity<>(sensorResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
            description = "Get all related temperature sensors with related camera",
            summary = "This is an endpoint for Get temperature sensors"
    )
    public ResponseEntity<List<TemperatureSensor>> getTemperatureSensors(
            @PathVariable UUID cameraId) {

        List<TemperatureSensor> sensors = temperatureSensorService.handleGetSensorsByCameraId(cameraId);
        return ResponseEntity.ok(sensors);
    }

    @PutMapping("/{sensorId}")
    @Operation(
            description = "Update temperature sensor with related camera",
            summary = "This is an endpoint for Update temperature sensor"
    )
    public ResponseEntity<SensorResponse> updateTemperatureSensor(
            @PathVariable UUID sensorId,
            @Valid @RequestBody SensorDto sensorDto, @PathVariable UUID cameraId) {

        TemperatureSensor sensorMetadata = sensorDtoConverter.toTemperatureEntity(sensorDto);
        TemperatureSensor updatedSensor = temperatureSensorService.handleUpdateSensor(cameraId, sensorId, sensorMetadata);
        SensorResponse sensorResponse = sensorDtoConverter.toSensorResponse(updatedSensor);
        return ResponseEntity.ok(sensorResponse);
    }

    @DeleteMapping("/{sensorId}")
    @Operation(
            description = "Delete temperature sensor with related camera",
            summary = "This is an endpoint for Delete temperature sensor"
    )
    public ResponseEntity<Void> deleteTemperatureSensor(
            @PathVariable UUID sensorId, @PathVariable UUID cameraId) {

        temperatureSensorService.handleDeleteSensor(cameraId, sensorId);
        return ResponseEntity.noContent().build();
    }
}
