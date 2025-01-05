package com.onboarding.camera.cameraonboarding.repository;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.entity.TemperatureSensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TemperatureSensorRepository extends JpaRepository<TemperatureSensor, UUID> {

    List<TemperatureSensor> findTemperatureSensorByCamera(Camera camera);
}
