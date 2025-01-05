package com.onboarding.camera.cameraonboarding.repository;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.entity.MotionSensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MotionSensorRepository extends JpaRepository<MotionSensor, UUID> {

    List<MotionSensor> findMotionSensorByCamera(Camera camera);
}
