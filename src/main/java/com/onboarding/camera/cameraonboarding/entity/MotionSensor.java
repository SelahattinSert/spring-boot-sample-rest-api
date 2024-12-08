package com.onboarding.camera.cameraonboarding.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MOTION")
public class MotionSensor extends Sensor {

}
