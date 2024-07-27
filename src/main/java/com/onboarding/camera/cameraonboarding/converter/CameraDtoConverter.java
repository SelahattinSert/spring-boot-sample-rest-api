package com.onboarding.camera.cameraonboarding.converter;

import com.onboarding.camera.cameraonboarding.dto.CameraDto;
import com.onboarding.camera.cameraonboarding.dto.CameraResponse;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import org.springframework.stereotype.Component;

@Component
public class CameraDtoConverter {

    public Camera toEntity(CameraDto cameraDto) {
        Camera camera = new Camera();
        camera.setCameraName(cameraDto.getCameraName());
        camera.setFirmwareVersion(cameraDto.getFirmwareVersion());
        return camera;
    }

    public CameraResponse toCameraResponse(Camera camera) {
        CameraResponse response = new CameraResponse();
        response.setCameraId(camera.getCamId());
        response.setCameraName(camera.getCameraName());
        response.setFirmwareVersion(camera.getFirmwareVersion());
        return response;
    }
}
