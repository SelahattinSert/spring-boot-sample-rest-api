package com.onboarding.camera.cameraonboarding.repository;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.entity.Sensor;
import com.onboarding.camera.cameraonboarding.entity.TemperatureSensor;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
class TemperatureSensorRepositoryTest {

    @Autowired
    TemperatureSensorRepository temperatureSensorRepository;

    @Autowired
    CameraRepository cameraRepository;

    private TemperatureSensor temperatureSensor;

    private final String SENSOR_NAME = "Temperature Sensor 1";
    private final String SENSOR_NAME_2 = "Temperature Sensor 2";
    private final SensorType SENSOR_TYPE = SensorType.TEMPERATURE;
    private final UUID NON_EXISTING_UUID = UUID.fromString("ef556dc0-0ddc-4f39-a96d-6886a54eee54");
    private final UUID CAMERA_ID = UUID.randomUUID();
    private final String CAMERA_NAME = "Camera 1";
    private final String FIRMWARE_VERSION = "v1.0";

    @BeforeEach
    void setUp() {
        temperatureSensor = new TemperatureSensor();
        temperatureSensor.setName(SENSOR_NAME);
        temperatureSensor.setSensorType(SENSOR_TYPE);
    }

    @Test
    public void expect_save_sensorRepository_saveAll_returnsSavedSensor() {

        // act
        Sensor savedSensor = temperatureSensorRepository.save(temperatureSensor);

        // assert
        Assertions.assertThat(savedSensor).isNotNull();
        Assertions.assertThat(savedSensor.getName()).isNotNull();
        Assertions.assertThat(savedSensor.getName()).isEqualTo(SENSOR_NAME);
        Assertions.assertThat(savedSensor.getSensorType()).isNotNull();
        Assertions.assertThat(savedSensor.getSensorType()).isEqualTo(SENSOR_TYPE);
        Assertions.assertThat(savedSensor.getId()).isNotNull();
    }

    @Test
    public void expect_save_sensorWithNullName_throwsException() {

        // arrange
        temperatureSensor.setName(null);

        // act and assert
        Assertions.assertThatThrownBy(() ->
                temperatureSensorRepository.saveAndFlush(temperatureSensor)
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void expect_save_sensorWithNullType_throwsException() {

        // arrange
        temperatureSensor.setSensorType(null);

        // act and assert
        Assertions.assertThatThrownBy(() ->
                temperatureSensorRepository.saveAndFlush(temperatureSensor)
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void expect_findById_temperatureSensorRepository_returnsSensor() {

        // arrange
        temperatureSensorRepository.save(temperatureSensor);

        // act
        Optional<TemperatureSensor> foundSensor = temperatureSensorRepository.findById(temperatureSensor.getId());

        //assert
        Assertions.assertThat(foundSensor).isPresent();
        Assertions.assertThat(foundSensor.get().getId()).isEqualTo(temperatureSensor.getId());
    }

    @Test
    public void expect_findById_sensorWithNotExistingId_throwsException() {

        // act
        Optional<TemperatureSensor> foundSensor = temperatureSensorRepository.findById(NON_EXISTING_UUID);

        // assert
        Assertions.assertThat(foundSensor).isEmpty();
    }

    @Test
    public void expect_delete_sensorWithExistingId_returnsVoid() {

        // Arrange
        TemperatureSensor savedSensor = temperatureSensorRepository.save(temperatureSensor);
        Camera associatedCamera = new Camera();
        associatedCamera.setCamId(CAMERA_ID);
        associatedCamera.setCameraName(CAMERA_NAME);
        associatedCamera.setFirmwareVersion(FIRMWARE_VERSION);
        savedSensor.setCamera(associatedCamera);
        temperatureSensorRepository.save(savedSensor);

        // Act
        temperatureSensorRepository.delete(savedSensor);

        // Assert
        Optional<TemperatureSensor> foundSensor = temperatureSensorRepository.findById(savedSensor.getId());
        Assertions.assertThat(foundSensor).isEmpty();
    }

    @Test
    public void expect_deleteCamera_removesAssociatedSensors() {

        // Arrange
        Camera camera = new Camera();
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);

        TemperatureSensor sensor1 = new TemperatureSensor();
        sensor1.setName(SENSOR_NAME);
        sensor1.setSensorType(SENSOR_TYPE);
        sensor1.setCamera(camera);

        TemperatureSensor sensor2 = new TemperatureSensor();
        sensor2.setName(SENSOR_NAME_2);
        sensor2.setSensorType(SENSOR_TYPE);
        sensor2.setCamera(camera);

        camera.getSensors().add(sensor1);
        camera.getSensors().add(sensor2);

        Camera savedCamera = cameraRepository.save(camera);
        UUID savedCameraId = savedCamera.getCamId();


        // Act
        cameraRepository.deleteById(savedCameraId);

        // Assert
        Assertions.assertThat(temperatureSensorRepository.findById(sensor1.getId())).isEmpty();
        Assertions.assertThat(temperatureSensorRepository.findById(sensor2.getId())).isEmpty();
        Assertions.assertThat(cameraRepository.findById(savedCameraId)).isEmpty();
    }

    @Test
    public void expect_findTemperatureSensorsByCamera_returnsTemperatureSensors() {
        // Arrange
        Camera camera = new Camera();
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);

        TemperatureSensor sensor1 = new TemperatureSensor();
        sensor1.setName(SENSOR_NAME);
        sensor1.setSensorType(SENSOR_TYPE);
        sensor1.setCamera(camera);

        TemperatureSensor sensor2 = new TemperatureSensor();
        sensor2.setName(SENSOR_NAME_2);
        sensor2.setSensorType(SENSOR_TYPE);
        sensor2.setCamera(camera);

        camera.getSensors().add(sensor1);
        camera.getSensors().add(sensor2);

        Camera savedCamera = cameraRepository.save(camera);

        // Act
        List<TemperatureSensor> sensors = temperatureSensorRepository.findTemperatureSensorByCamera(savedCamera);

        // Assert
        Assertions.assertThat(sensors).hasSize(2);
        Assertions.assertThat(sensors).extracting(TemperatureSensor::getName)
                .containsExactlyInAnyOrder(SENSOR_NAME, SENSOR_NAME_2);
    }

    @Test
    public void expect_updateSensor_withValidSensor_returnsUpdatedSensor() {
        // Arrange
        TemperatureSensor savedSensor = temperatureSensorRepository.save(temperatureSensor);
        String updatedName = "Updated Sensor Name";
        String updatedSensorVersion = "v1.1";
        String updatedSensorData = "Sensor Data";

        // Act
        savedSensor.setName(updatedName);
        savedSensor.setVersion(updatedSensorVersion);
        savedSensor.setData(updatedSensorData);
        TemperatureSensor updatedSensor = temperatureSensorRepository.save(savedSensor);

        // Assert
        Assertions.assertThat(updatedSensor).isNotNull();
        Assertions.assertThat(updatedSensor.getName()).isEqualTo(updatedName);
        Assertions.assertThat(updatedSensor.getVersion()).isEqualTo(updatedSensorVersion);
        Assertions.assertThat(updatedSensor.getData()).isEqualTo(updatedSensorData);
    }
}