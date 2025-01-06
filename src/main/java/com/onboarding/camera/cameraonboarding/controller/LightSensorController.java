package com.onboarding.camera.cameraonboarding.controller;

import com.onboarding.camera.cameraonboarding.converter.SensorDtoConverter;
import com.onboarding.camera.cameraonboarding.dto.SensorDto;
import com.onboarding.camera.cameraonboarding.dto.SensorResponse;
import com.onboarding.camera.cameraonboarding.entity.LightSensor;
import com.onboarding.camera.cameraonboarding.service.impl.LightSensorService;
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
@RequestMapping("${api.version}/camera/{cameraId}/sensor/light")
@RequiredArgsConstructor
@Tag(name = "Light Sensor Controller")
public class LightSensorController {

    private final LightSensorService lightSensorService;

    private final SensorDtoConverter sensorDtoConverter;

    @PostMapping
    @Operation(
            description = "Add light sensor to the related camera",
            summary = "This is an endpoint for Add light sensor"
    )
    public ResponseEntity<SensorResponse> addLightSensor(
            @PathVariable UUID cameraId,
            @Valid @RequestBody SensorDto sensorDto) {

        LightSensor sensorMetadata = sensorDtoConverter.toLightEntity(sensorDto);
        LightSensor savedSensor = lightSensorService.handleCreateSensor(cameraId, sensorMetadata);
        SensorResponse sensorResponse = sensorDtoConverter.toSensorResponse(savedSensor);

        return new ResponseEntity<>(sensorResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
            description = "Get all related light sensors with related camera",
            summary = "This is an endpoint for Get light sensors"
    )
    public ResponseEntity<List<LightSensor>> getLightSensors(
            @PathVariable UUID cameraId) {

        List<LightSensor> sensors = lightSensorService.handleGetSensorsByCameraId(cameraId);
        return ResponseEntity.ok(sensors);
    }

    @PutMapping("/{sensorId}")
    @Operation(
            description = "Update light sensor with related camera",
            summary = "This is an endpoint for Update light sensor"
    )
    public ResponseEntity<SensorResponse> updateLightSensor(
            @PathVariable UUID sensorId,
            @Valid @RequestBody SensorDto sensorDto, @PathVariable UUID cameraId) {

        LightSensor sensorMetadata = sensorDtoConverter.toLightEntity(sensorDto);
        LightSensor updatedSensor = lightSensorService.handleUpdateSensor(cameraId, sensorId, sensorMetadata);
        SensorResponse sensorResponse = sensorDtoConverter.toSensorResponse(updatedSensor);
        return ResponseEntity.ok(sensorResponse);
    }

    @DeleteMapping("/{sensorId}")
    @Operation(
            description = "Delete light sensor with related camera",
            summary = "This is an endpoint for Delete light sensor"
    )
    public ResponseEntity<Void> deleteLightSensor(
            @PathVariable UUID sensorId, @PathVariable UUID cameraId) {

        lightSensorService.handleDeleteSensor(cameraId, sensorId);
        return ResponseEntity.noContent().build();
    }
}
