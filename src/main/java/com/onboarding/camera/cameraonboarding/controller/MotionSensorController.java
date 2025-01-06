package com.onboarding.camera.cameraonboarding.controller;

import com.onboarding.camera.cameraonboarding.converter.SensorDtoConverter;
import com.onboarding.camera.cameraonboarding.dto.SensorDto;
import com.onboarding.camera.cameraonboarding.dto.SensorResponse;
import com.onboarding.camera.cameraonboarding.entity.MotionSensor;
import com.onboarding.camera.cameraonboarding.service.impl.MotionSensorService;
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
@RequestMapping("${api.version}/camera/{cameraId}/sensor/motion")
@RequiredArgsConstructor
@Tag(name = "Motion Sensor Controller")
public class MotionSensorController {

    private final MotionSensorService motionSensorService;

    private final SensorDtoConverter sensorDtoConverter;

    @PostMapping
    @Operation(
            description = "Add motion sensor to the related camera",
            summary = "This is an endpoint for Add motion sensor"
    )
    public ResponseEntity<SensorResponse> addMotionSensor(
            @PathVariable UUID cameraId,
            @Valid @RequestBody SensorDto sensorDto) {

        MotionSensor motionSensor = sensorDtoConverter.toMotionEntity(sensorDto);
        MotionSensor savedSensor = motionSensorService.handleCreateSensor(cameraId, motionSensor);
        SensorResponse sensorResponse = sensorDtoConverter.toSensorResponse(savedSensor);

        return new ResponseEntity<>(sensorResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
            description = "Get all related motion sensors with related camera",
            summary = "This is an endpoint for Get motion sensors"
    )
    public ResponseEntity<List<MotionSensor>> getMotionSensors(
            @PathVariable UUID cameraId) {

        List<MotionSensor> sensors = motionSensorService.handleGetSensorsByCameraId(cameraId);
        return ResponseEntity.ok(sensors);
    }

    @PutMapping("/{sensorId}")
    @Operation(
            description = "Update motion sensor with related camera",
            summary = "This is an endpoint for Update motion sensor"
    )
    public ResponseEntity<SensorResponse> updateMotionSensor(
            @PathVariable UUID sensorId,
            @Valid @RequestBody SensorDto sensorDto, @PathVariable UUID cameraId) {

        MotionSensor motionSensor = sensorDtoConverter.toMotionEntity(sensorDto);
        MotionSensor updatedSensor = motionSensorService.handleUpdateSensor(cameraId, sensorId, motionSensor);
        SensorResponse sensorResponse = sensorDtoConverter.toSensorResponse(updatedSensor);
        return ResponseEntity.ok(sensorResponse);
    }

    @DeleteMapping("/{sensorId}")
    @Operation(
            description = "Delete motion sensor with related camera",
            summary = "This is an endpoint for Delete motion sensor"
    )
    public ResponseEntity<Void> deleteMotionSensor(
            @PathVariable UUID sensorId, @PathVariable UUID cameraId) {

        motionSensorService.handleDeleteSensor(cameraId, sensorId);
        return ResponseEntity.noContent().build();
    }
}
