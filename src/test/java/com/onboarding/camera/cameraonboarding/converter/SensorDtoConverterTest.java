package com.onboarding.camera.cameraonboarding.converter;

import com.onboarding.camera.cameraonboarding.dto.SensorDto;
import com.onboarding.camera.cameraonboarding.dto.SensorResponse;
import com.onboarding.camera.cameraonboarding.entity.Sensor;
import com.onboarding.camera.cameraonboarding.entity.TemperatureSensor;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import com.onboarding.camera.cameraonboarding.exception.SensorMismatchException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotCreatedException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class SensorDtoConverterTest {

    private SensorDtoConverter sensorDtoConverter;

    private final UUID SENSOR_ID = UUID.randomUUID();
    private final String SENSOR_NAME = "Sensor 1";
    private final String SENSOR_VERSION = "v1.0";
    private final SensorType SENSOR_TYPE_LIGHT = SensorType.LIGHT;
    private final String SENSOR_DATA = "Sensor data";
    private final SensorType SENSOR_TYPE_TEMPERATURE = SensorType.TEMPERATURE;

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
        sensorDto.setSensorType(SENSOR_TYPE_LIGHT);
        sensorDto.setData(SENSOR_DATA);

        // act
        Sensor sensor = sensorDtoConverter.toLightEntity(sensorDto);

        // assert
        Assertions.assertThat(sensor).isNotNull();
        Assertions.assertThat(sensor.getName()).isEqualTo(SENSOR_NAME);
        Assertions.assertThat(sensor.getVersion()).isEqualTo(SENSOR_VERSION);
        Assertions.assertThat(sensor.getSensorType()).isEqualTo(SENSOR_TYPE_LIGHT);
        Assertions.assertThat(sensor.getData()).isEqualTo(SENSOR_DATA);
    }

    @Test
    void expect_convert_withValidSensor_returnSensorDto() {
        // arrange
        Sensor sensor = new TemperatureSensor();
        sensor.setId(SENSOR_ID);
        sensor.setName(SENSOR_NAME);
        sensor.setVersion(SENSOR_VERSION);
        sensor.setSensorType(SENSOR_TYPE_LIGHT);
        sensor.setData(SENSOR_DATA);

        // act
        SensorResponse response = sensorDtoConverter.toSensorResponse(sensor);

        // assert
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(SENSOR_ID);
        Assertions.assertThat(response.getName()).isEqualTo(SENSOR_NAME);
        Assertions.assertThat(response.getVersion()).isEqualTo(SENSOR_VERSION);
        Assertions.assertThat(response.getSensorType()).isEqualTo(SENSOR_TYPE_LIGHT);
        Assertions.assertThat(response.getData()).isEqualTo(SENSOR_DATA);
    }

    @Test
    void expect_convert_withNullSensorName_throwsSensorNotCreatedException() {
        // arrange
        SensorDto sensorDto = new SensorDto();
        sensorDto.setName(null);


        // act and assert
        Assertions.assertThatThrownBy(() -> sensorDtoConverter.toLightEntity(sensorDto))
                .isInstanceOf(SensorNotCreatedException.class)
                .hasMessage("Sensor name cannot be null");
    }

    @Test
    void expect_convert_withNullSensorVersion_throwsSensorNotCreatedException() {
        // arrange
        SensorDto sensorDto = new SensorDto();
        sensorDto.setName(SENSOR_NAME);
        sensorDto.setVersion(null);


        // act and assert
        Assertions.assertThatThrownBy(() -> sensorDtoConverter.toLightEntity(sensorDto))
                .isInstanceOf(SensorNotCreatedException.class)
                .hasMessage("Sensor version cannot be null");
    }

    @Test
    void expect_convert_withNullSensorType_throwsSensorNotCreatedException() {
        // arrange
        SensorDto sensorDto = new SensorDto();
        sensorDto.setName(SENSOR_NAME);
        sensorDto.setVersion(SENSOR_VERSION);
        sensorDto.setSensorType(null);


        // act and assert
        Assertions.assertThatThrownBy(() -> sensorDtoConverter.toLightEntity(sensorDto))
                .isInstanceOf(SensorNotCreatedException.class)
                .hasMessage("Sensor type cannot be null");
    }

    @Test
    void expect_convertToLightEntity_withWrongSensorType_throwsSensorMismatchException() {
        // arrange
        SensorDto sensorDto = new SensorDto();
        sensorDto.setName(SENSOR_NAME);
        sensorDto.setVersion(SENSOR_VERSION);
        sensorDto.setSensorType(SENSOR_TYPE_TEMPERATURE);


        // act and assert
        Assertions.assertThatThrownBy(() -> sensorDtoConverter.toLightEntity(sensorDto))
                .isInstanceOf(SensorMismatchException.class)
                .hasMessage("Invalid sensor type for LightSensor");
    }

    @Test
    void expect_convertToTemperatureEntity_withWrongSensorType_throwsSensorMismatchException() {
        // arrange
        SensorDto sensorDto = new SensorDto();
        sensorDto.setName(SENSOR_NAME);
        sensorDto.setVersion(SENSOR_VERSION);
        sensorDto.setSensorType(SENSOR_TYPE_LIGHT);


        // act and assert
        Assertions.assertThatThrownBy(() -> sensorDtoConverter.toTemperatureEntity(sensorDto))
                .isInstanceOf(SensorMismatchException.class)
                .hasMessage("Invalid sensor type for TemperatureSensor");
    }
}