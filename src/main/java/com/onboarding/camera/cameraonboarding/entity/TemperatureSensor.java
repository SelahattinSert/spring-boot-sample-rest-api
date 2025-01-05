package com.onboarding.camera.cameraonboarding.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.ToString;

@Entity
@ToString
@DiscriminatorValue("TEMPERATURE")
public class TemperatureSensor extends Sensor {

}
