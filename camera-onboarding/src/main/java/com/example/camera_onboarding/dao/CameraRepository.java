package com.example.camera_onboarding.dao;

import com.example.camera_onboarding.entity.Camera;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CameraRepository extends JpaRepository<Camera, Integer> {
}
