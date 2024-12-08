package com.onboarding.camera.cameraonboarding.repository;

import com.onboarding.camera.cameraonboarding.entity.LightSensor;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LightSensorRepository extends JpaRepository<LightSensor, UUID> {

    @Query("SELECT s FROM LightSensor s WHERE s.camera.camId = :cameraId AND s.sensorType = :sensorType")
    List<LightSensor> findByCameraIdAndSensorType(@Param("cameraId") UUID cameraId, @Param("sensorType") SensorType sensorType);
}
