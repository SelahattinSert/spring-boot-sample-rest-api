package com.onboarding.camera.cameraonboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.logging.Logger;

@SpringBootApplication
public class CameraOnboardingApplication {

	private static Logger logger = Logger.getLogger(CameraOnboardingApplication.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(CameraOnboardingApplication.class, args);
		logger.info("Application is started");
	}

}
