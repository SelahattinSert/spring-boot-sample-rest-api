package com.onboarding.camera.cameraonboarding.repository;

import com.onboarding.camera.cameraonboarding.entity.TemperatureSensor;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TemperatureSensorRepository extends JpaRepository<TemperatureSensor, UUID> {

    @Query("SELECT s FROM TemperatureSensor s WHERE s.camera.camId = :cameraId AND s.sensorType = :sensorType")
    List<TemperatureSensor> findByCameraIdAndSensorType(@Param("cameraId") UUID cameraId, @Param("sensorType") SensorType sensorType);
}
