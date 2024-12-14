package com.onboarding.camera.cameraonboarding.repository;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.entity.LightSensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LightSensorRepository extends JpaRepository<LightSensor, UUID> {

    List<LightSensor> findLightSensorByCamera(Camera camera);
}
