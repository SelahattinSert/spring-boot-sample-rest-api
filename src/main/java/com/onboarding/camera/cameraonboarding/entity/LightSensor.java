package com.onboarding.camera.cameraonboarding.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("LIGHT")
public class LightSensor extends Sensor {

}
