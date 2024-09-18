package com.onboarding.camera.cameraonboarding.controller;

import com.onboarding.camera.cameraonboarding.converter.CameraDtoConverter;
import com.onboarding.camera.cameraonboarding.dto.CameraDto;
import com.onboarding.camera.cameraonboarding.dto.CameraResponse;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.version}")
public class CameraRestController {

    private final CameraService cameraService;

    private final CameraDtoConverter cameraDtoConverter;

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

    @GetMapping("/camera/{camera_id}")
    public ResponseEntity<CameraResponse> getCameraMetadata(@Valid @PathVariable UUID camera_id) {

        Camera camera = cameraService.getCameraById(camera_id);
        CameraResponse cameraResponse = cameraDtoConverter.toCameraResponse(camera);

        return new ResponseEntity<>(cameraResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/camera/{camera_id}/upload_image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadImage(@PathVariable UUID camera_id,
                                              @RequestParam("imageId") UUID imageId,
                                              @RequestParam("data") byte[] imageData) {

        cameraService.handleUploadImage(camera_id, imageId, imageData);

        return ResponseEntity.ok().body("Uploaded");
    }
}
