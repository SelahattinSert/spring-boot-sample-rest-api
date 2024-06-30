package com.example.camera_onboarding.rest;

import com.example.camera_onboarding.entity.Camera;
import com.example.camera_onboarding.service.CameraService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CameraRestController {

    private final CameraService cameraService;

    private static final Logger logger = LogManager.getLogger(CameraRestController.class);

    @Autowired
    public CameraRestController(CameraService cameraService) {
        this.cameraService = cameraService;
    }

    @GetMapping("/cameras")
    public List<Camera> getAllCameras() {

        logger.info("Received request to fetch all cameras");
        List<Camera> cameras = cameraService.getAllCameras();
        logger.debug("Fetched cameras: ");

        return cameras;
    }

    @GetMapping("/cameras/{cameraId}")
    public Camera getCamera(@PathVariable Integer cameraId) {

        logger.info("Received request to fetch camera with id: {}", cameraId);

        Camera camera = cameraService.getCamera(cameraId);

        if (camera == null) {
            logger.error("Camera not found with id: {}", cameraId);
            throw new RuntimeException("Camera not found with id: " + cameraId);
        }

        logger.debug("Camera found: {}", camera);
        return camera;
    }

    @PostMapping("/cameras")
    public Camera createCamera(@RequestBody Camera camera) {

        logger.info("Received request to create a new camera: {}", camera);

        // this is to force a save of new item ... instead of update

        camera.setId(0);

        Camera dbCamera = cameraService.saveCamera(camera);

        logger.debug("Created camera: {}", dbCamera);

        return dbCamera;
    }

    @PutMapping("/cameras")
    public Camera modifyCamera(@RequestBody Camera camera) {

        logger.info("Received request to modify camera: {}", camera);

        Camera dbCamera = cameraService.getCamera(camera.getId());

        if (dbCamera == null) {
            logger.error("Camera not found with id (modify camera): {}", camera.getId());
            throw new RuntimeException("Camera not found with id: " + camera.getId());
        }

        logger.debug("Updated camera: {}", dbCamera);

        return dbCamera;
    }

    @DeleteMapping("/cameras/{cameraId}")
    public String deleteCamera(@RequestBody int cameraId) {

        logger.info("Received request to delete camera with id: {}", cameraId);

        Camera tempCamera = cameraService.getCamera(cameraId);

        if (tempCamera == null) {
            logger.error("Camera not found with id (delete camera): {}", cameraId);
            throw new RuntimeException("Camera not found with id: " + cameraId);
        }

        cameraService.deleteCamera(cameraId);
        logger.debug("Deleted camera: {}", tempCamera);

        return "Camera deleted succesfully" + tempCamera.getName();
    }
}
