package com.onboarding.camera.cameraonboarding.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TEMPERATURE")
public class TemperatureSensor extends Sensor {

}
