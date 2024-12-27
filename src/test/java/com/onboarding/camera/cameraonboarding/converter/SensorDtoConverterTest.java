package com.onboarding.camera.cameraonboarding.converter;

import com.onboarding.camera.cameraonboarding.dto.SensorDto;
import com.onboarding.camera.cameraonboarding.dto.SensorResponse;
import com.onboarding.camera.cameraonboarding.entity.Sensor;
import com.onboarding.camera.cameraonboarding.entity.TemperatureSensor;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class SensorDtoConverterTest {

    private SensorDtoConverter sensorDtoConverter;

    private final UUID SENSOR_ID = UUID.randomUUID();
    private final String SENSOR_NAME = "Sensor 1";
    private final String SENSOR_VERSION = "v1.0";
    private final SensorType SENSOR_TYPE = SensorType.LIGHT;
    private final String SENSOR_DATA = "Sensor data";

    @BeforeEach
    void setUp() {
        sensorDtoConverter = new SensorDtoConverter();
    }

    @Test
    void expect_convert_withValidSensorDto_returnSensor() {
        // arrange
        SensorDto sensorDto = new SensorDto();
        sensorDto.setName(SENSOR_NAME);
        sensorDto.setVersion(SENSOR_VERSION);
        sensorDto.setSensorType(SENSOR_TYPE);
        sensorDto.setData(SENSOR_DATA);

        // act
        Sensor sensor = sensorDtoConverter.toLightEntity(sensorDto);

        // assert
        AssertionsForClassTypes.assertThat(sensor).isNotNull();
        AssertionsForClassTypes.assertThat(sensor.getName()).isEqualTo(SENSOR_NAME);
        AssertionsForClassTypes.assertThat(sensor.getVersion()).isEqualTo(SENSOR_VERSION);
        AssertionsForClassTypes.assertThat(sensor.getSensorType()).isEqualTo(SENSOR_TYPE);
        AssertionsForClassTypes.assertThat(sensor.getData()).isEqualTo(SENSOR_DATA);
    }

    @Test
    void expect_convert_withValidSensor_returnSensorDto() {
        // arrange
        Sensor sensor = new TemperatureSensor();
        sensor.setId(SENSOR_ID);
        sensor.setName(SENSOR_NAME);
        sensor.setVersion(SENSOR_VERSION);
        sensor.setSensorType(SENSOR_TYPE);
        sensor.setData(SENSOR_DATA);

        // act
        SensorResponse response = sensorDtoConverter.toSensorResponse(sensor);

        // assert
        AssertionsForClassTypes.assertThat(response).isNotNull();
        AssertionsForClassTypes.assertThat(response.getId()).isEqualTo(SENSOR_ID);
        AssertionsForClassTypes.assertThat(response.getName()).isEqualTo(SENSOR_NAME);
        AssertionsForClassTypes.assertThat(response.getVersion()).isEqualTo(SENSOR_VERSION);
        AssertionsForClassTypes.assertThat(response.getSensorType()).isEqualTo(SENSOR_TYPE);
        AssertionsForClassTypes.assertThat(response.getData()).isEqualTo(SENSOR_DATA);
    }
}