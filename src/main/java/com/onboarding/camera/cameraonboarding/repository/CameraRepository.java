package com.onboarding.camera.cameraonboarding.repository;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CameraRepository extends JpaRepository<Camera, UUID> {
}
