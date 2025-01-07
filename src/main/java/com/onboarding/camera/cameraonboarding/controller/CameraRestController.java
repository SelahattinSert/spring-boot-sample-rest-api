package com.onboarding.camera.cameraonboarding.controller;

import com.onboarding.camera.cameraonboarding.converter.CameraDtoConverter;
import com.onboarding.camera.cameraonboarding.converter.LocationDtoConverter;
import com.onboarding.camera.cameraonboarding.dto.CameraDto;
import com.onboarding.camera.cameraonboarding.dto.CameraResponse;
import com.onboarding.camera.cameraonboarding.dto.LocationDto;
import com.onboarding.camera.cameraonboarding.dto.LocationResponse;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.entity.Location;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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

import java.util.Base64;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.version}")
@Tag(name = "Camera Controller")
public class CameraRestController {

    private final CameraService cameraService;

    private final CameraDtoConverter cameraDtoConverter;

    private final LocationDtoConverter locationDtoConverter;

    @PostMapping("/onboard")
    @Operation(
            description = "Saves a new camera and returns the saved camera details",
            summary = "This is an endpoint for Save a new camera",
            responses = {
                    @ApiResponse(
                            description = "Created camera",
                            responseCode = "201",
                            content = @Content(
                                    schema = @Schema(implementation = CameraResponse.class)
                            )
                    ),
                    @ApiResponse(
                            description = "Camera can not created",
                            responseCode = "500"
                    )
            }
    )
    public ResponseEntity<CameraResponse> saveCamera(@Valid @RequestBody CameraDto cameraDto) {

        Camera camera = cameraDtoConverter.toEntity(cameraDto);
        Camera savedCamera = cameraService.handleSaveCamera(camera);
        CameraResponse cameraResponse = cameraDtoConverter.toCameraResponse(savedCamera);

        return new ResponseEntity<>(cameraResponse, HttpStatus.CREATED);
    }

    @PatchMapping("/{camera_id}/initialize")
    @Operation(
            description = "Initializes the camera",
            summary = "This is an endpoint for Initialize camera",
            responses = {
                    @ApiResponse(
                            description = "Initialized camera",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Camera already initialized",
                            responseCode = "409"
                    ),
                    @ApiResponse(
                            description = "Camera can not initialized",
                            responseCode = "500"
                    )
            }
    )
    public ResponseEntity<Void> initializeCamera(@Valid @PathVariable UUID camera_id) {

        cameraService.handleInitializeCamera(camera_id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/camera/{camera_id}")
    @Operation(
            description = "Retrieves the metadata of a camera",
            summary = "This is an endpoint for Get camera metadata",
            responses = {
                    @ApiResponse(
                            description = "Get camera metadata",
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = CameraResponse.class)
                            )
                    ),
                    @ApiResponse(
                            description = "Camera not found",
                            responseCode = "404"
                    )
            }
    )
    public ResponseEntity<CameraResponse> getCameraMetadata(@Valid @PathVariable UUID camera_id) {

        Camera camera = cameraService.getCameraById(camera_id);
        CameraResponse cameraResponse = cameraDtoConverter.toCameraResponse(camera);

        return new ResponseEntity<>(cameraResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/camera/{camera_id}/upload_image")
    @Operation(
            description = "Uploads an image for a specific camera",
            summary = "This is an endpoint for Upload an image",
            responses = {
                    @ApiResponse(
                            description = "Uploaded image",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Image already uploaded",
                            responseCode = "409"
                    ),
                    @ApiResponse(
                            description = "Image not uploaded",
                            responseCode = "500"
                    )
            }
    )
    public ResponseEntity<String> uploadImage(@PathVariable UUID camera_id,
                                              @RequestParam("imageId") UUID imageId,
                                              @RequestParam("data") String imageData) {

        cameraService.handleUploadImage(camera_id, imageId, Base64.getDecoder().decode(imageData));

        return ResponseEntity.ok().body("Uploaded");
    }

    @GetMapping(path = "/camera/{cameraId}/download_image", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(
            description = "Downloads an image associated with a specific camera",
            summary = "This is an endpoint for Download an image",
            responses = {
                    @ApiResponse(
                            description = "Downloaded image",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Camera not found",
                            responseCode = "404"
                    ),
                    @ApiResponse(
                            description = "Camera can not initialized",
                            responseCode = "500"
                    ),
                    @ApiResponse(
                            description = "Image not found",
                            responseCode = "404"
                    ),
                    @ApiResponse(
                            description = "Image not downloaded",
                            responseCode = "500"
                    )
            }
    )
    public ResponseEntity<byte[]> downloadImage(@PathVariable UUID cameraId) {

        byte[] imageData = cameraService.handleDownloadImage(cameraId);
        Camera camera = cameraService.getCameraById(cameraId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + camera.getImageId() + ".png")
                .body(imageData);
    }

    @PostMapping("/camera/{cameraId}/location")
    @Operation(
            description = "Adds location details to a specific camera and returns the updated location",
            summary = "This is an endpoint for Add location to camera",
            responses = {
                    @ApiResponse(
                            description = "Added location",
                            responseCode = "201",
                            content = @Content(
                                    schema = @Schema(implementation = LocationResponse.class)
                            )
                    ),
                    @ApiResponse(
                            description = "Camera not found",
                            responseCode = "404"
                    ),
                    @ApiResponse(
                            description = "Location can not added",
                            responseCode = "500"
                    )
            }
    )
    public ResponseEntity<LocationResponse> addLocation(
            @PathVariable UUID cameraId,
            @Valid @RequestBody LocationDto locationDto) {

        Camera updatedCamera = cameraService.handleAddLocation(cameraId, locationDto);
        Location cameraLocation = updatedCamera.getLocation();
        LocationResponse response = locationDtoConverter.toLocationResponse(cameraLocation);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
