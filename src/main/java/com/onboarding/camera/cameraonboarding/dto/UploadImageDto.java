package com.onboarding.camera.cameraonboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UploadImageDto {

    private UUID imageId;

    private MultipartFile file;
}
