package com.onboarding.camera.cameraonboarding.controller;

import com.onboarding.camera.cameraonboarding.converter.CameraDtoConverter;
import com.onboarding.camera.cameraonboarding.dto.CameraDto;
import com.onboarding.camera.cameraonboarding.dto.CameraResponse;
import com.onboarding.camera.cameraonboarding.dto.UploadImageDto;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import jakarta.validation.Valid;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("${api.version}")
public class CameraRestController {

    private final CameraService cameraService;

    private final CameraDtoConverter cameraDtoConverter;

    public CameraRestController(final CameraService cameraService, final CameraDtoConverter cameraDtoConverter) {
        this.cameraService = cameraService;
        this.cameraDtoConverter = cameraDtoConverter;
    }

    @PostMapping("/onboard")
    public ResponseEntity<CameraResponse> saveCamera(@Valid @RequestBody final CameraDto cameraDto) {

        final Camera camera = cameraDtoConverter.toEntity(cameraDto);
        final Camera savedCamera = cameraService.handleSaveCamera(camera);
        final CameraResponse cameraResponse = cameraDtoConverter.toCameraResponse(savedCamera);

        return new ResponseEntity<>(cameraResponse, HttpStatus.CREATED);
    }

    @PatchMapping("/{camera_id}/initialize")
    public ResponseEntity<Void> initializeCamera(@Valid @PathVariable final UUID camera_id) {

        cameraService.handleInitializeCamera(camera_id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/camera/{camera_id}")
    public ResponseEntity<CameraResponse> getCameraMetadata(@Valid @PathVariable final UUID camera_id) {

        final Camera camera = cameraService.getCameraById(camera_id);
        final CameraResponse cameraResponse = cameraDtoConverter.toCameraResponse(camera);

        return new ResponseEntity<>(cameraResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/camera/{camera_id}/upload_image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadImage(@PathVariable final UUID camera_id,
                                              @RequestParam("imageId") final UUID imageId,
                                              @RequestParam("file") final MultipartFile file) {

        final UploadImageDto uploadImageDto = new UploadImageDto(imageId, file);

        cameraService.handleUploadImage(camera_id, uploadImageDto);

        return ResponseEntity.ok().body("Uploaded");
    }
}
