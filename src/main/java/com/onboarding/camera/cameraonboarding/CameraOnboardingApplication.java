package com.onboarding.camera.cameraonboarding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class CameraOnboardingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CameraOnboardingApplication.class, args);
		log.info("Application is started");
	}
}
