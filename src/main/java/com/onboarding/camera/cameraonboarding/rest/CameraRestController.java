package com.onboarding.camera.cameraonboarding.rest;

import com.onboarding.camera.cameraonboarding.dto.CameraDto;
import com.onboarding.camera.cameraonboarding.dto.CameraResponse;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class CameraRestController {

    private CameraService cameraService;

    private static final Logger logger = LoggerFactory.getLogger(CameraRestController.class);

    @Autowired
    public CameraRestController(CameraService cameraService) {
        this.cameraService = cameraService;
    }

    @PostMapping("/onboard")
    public ResponseEntity<CameraResponse> saveCamera(@RequestBody CameraDto cameraDto) {

        CameraResponse responseDto = new CameraResponse();
        Camera camera = new Camera();
        try {
            camera.setCameraName(cameraDto.getCameraName());
            camera.setFirmwareVersion(cameraDto.getFirmwareVersion());
            Camera savedCamera = cameraService.saveCamera(camera);
            responseDto.setMessage("Onboarded");
            responseDto.setCameraName(savedCamera.getCameraName());
            responseDto.setFirmwareVersion(savedCamera.getFirmwareVersion());
            logger.info("Saved camera with ID: {}", savedCamera.getCamId());
            return ResponseEntity.ok(responseDto);
        }
        catch (Exception ex) {
            logger.error("Exception occurred while saving camera: {}", ex.getMessage());
            responseDto.setMessage(ex.getMessage());
            return ResponseEntity.status(500).body(responseDto);
        }
    }
}
