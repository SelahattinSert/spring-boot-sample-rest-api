package com.onboarding.camera.cameraonboarding.controller;

import com.onboarding.camera.cameraonboarding.converter.CameraDtoConverter;
import com.onboarding.camera.cameraonboarding.dto.CameraDto;
import com.onboarding.camera.cameraonboarding.dto.CameraResponse;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.version}")
public class CameraRestController {

    private final CameraService cameraService;

    private final CameraDtoConverter cameraDtoConverter;

    public CameraRestController(CameraService cameraService, CameraDtoConverter cameraDtoConverter) {
        this.cameraService = cameraService;
        this.cameraDtoConverter = cameraDtoConverter;
    }

    @PostMapping("/onboard")
    public ResponseEntity<CameraResponse> saveCamera(@Valid @RequestBody CameraDto cameraDto) {

        Camera camera = cameraDtoConverter.toEntity(cameraDto);
        Camera savedCamera = cameraService.handleSaveCamera(camera);
        CameraResponse cameraResponse = cameraDtoConverter.toCameraResponse(savedCamera);

        return new ResponseEntity<>(cameraResponse, HttpStatus.CREATED);
    }

    @PatchMapping("/{camera_id}/initialize")
    public ResponseEntity<Void> initializeCamera(@Valid @PathVariable UUID camera_id) {

        cameraService.handleInitializeCamera(camera_id);

        return ResponseEntity.ok().build();
    }
}
