package com.onboarding.camera.cameraonboarding.repository;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.entity.LightSensor;
import com.onboarding.camera.cameraonboarding.entity.Sensor;
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
class LightSensorRepositoryTest {

    @Autowired
    LightSensorRepository lightSensorRepository;

    @Autowired
    CameraRepository cameraRepository;

    private LightSensor lightSensor;

    private final String SENSOR_NAME = "Light Sensor 1";
    private final String SENSOR_NAME_2 = "Light Sensor 2";
    private final SensorType SENSOR_TYPE = SensorType.LIGHT;
    private final UUID NON_EXISTING_UUID = UUID.fromString("ef556dc0-0ddc-4f39-a96d-6886a54eee54");
    private final UUID CAMERA_ID = UUID.randomUUID();
    private final String CAMERA_NAME = "Camera 1";
    private final String FIRMWARE_VERSION = "v1.0";

    @BeforeEach
    void setUp() {
        lightSensor = new LightSensor();
        lightSensor.setName(SENSOR_NAME);
        lightSensor.setSensorType(SENSOR_TYPE);
    }

    @Test
    public void expect_save_sensorRepository_saveAll_returnsSavedSensor() {

        // act
        Sensor savedSensor = lightSensorRepository.save(lightSensor);

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
        lightSensor.setName(null);

        // act and assert
        Assertions.assertThatThrownBy(() ->
                lightSensorRepository.saveAndFlush(lightSensor)
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void expect_save_sensorWithNullType_throwsException() {

        // arrange
        lightSensor.setSensorType(null);

        // act and assert
        Assertions.assertThatThrownBy(() ->
                lightSensorRepository.saveAndFlush(lightSensor)
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void expect_findById_lightSensorRepository_returnsSensor() {

        // arrange
        lightSensorRepository.save(lightSensor);

        // act
        Optional<LightSensor> foundSensor = lightSensorRepository.findById(lightSensor.getId());

        //assert
        Assertions.assertThat(foundSensor).isPresent();
        Assertions.assertThat(foundSensor.get().getId()).isEqualTo(lightSensor.getId());
    }

    @Test
    public void expect_findById_sensorWithNotExistingId_throwsException() {

        // act
        Optional<LightSensor> foundSensor = lightSensorRepository.findById(NON_EXISTING_UUID);

        // assert
        Assertions.assertThat(foundSensor).isEmpty();
    }

    @Test
    public void expect_delete_sensorWithExistingId_returnsVoid() {

        // Arrange
        LightSensor savedSensor = lightSensorRepository.save(lightSensor);
        Camera associatedCamera = new Camera();
        associatedCamera.setCamId(CAMERA_ID);
        associatedCamera.setCameraName(CAMERA_NAME);
        associatedCamera.setFirmwareVersion(FIRMWARE_VERSION);
        savedSensor.setCamera(associatedCamera);
        lightSensorRepository.save(savedSensor);

        // Act
        lightSensorRepository.delete(savedSensor);

        // Assert
        Optional<LightSensor> foundSensor = lightSensorRepository.findById(savedSensor.getId());
        Assertions.assertThat(foundSensor).isEmpty();
    }

    @Test
    public void expect_deleteCamera_removesAssociatedSensors() {

        // Arrange
        Camera camera = new Camera();
        camera.setCamId(CAMERA_ID);
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);
        camera = cameraRepository.save(camera);

        LightSensor sensor1 = new LightSensor();
        sensor1.setName(SENSOR_NAME);
        sensor1.setSensorType(SENSOR_TYPE);
        sensor1.setCamera(camera);

        LightSensor sensor2 = new LightSensor();
        sensor2.setName(SENSOR_NAME_2);
        sensor2.setSensorType(SENSOR_TYPE);
        sensor2.setCamera(camera);

        camera.getSensors().add(sensor1);
        camera.getSensors().add(sensor2);

        lightSensorRepository.save(sensor1);
        lightSensorRepository.save(sensor2);

        // Act
        cameraRepository.delete(camera);

        // Assert
        Assertions.assertThat(lightSensorRepository.findLightSensorByCamera(camera)).isEmpty();
    }

    @Test
    public void expect_findLightSensorsByCamera_returnsLightSensors() {
        // Arrange
        Camera camera = new Camera();
        camera.setCamId(CAMERA_ID);
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);
        Camera savedCamera = cameraRepository.save(camera);

        LightSensor sensor1 = new LightSensor();
        sensor1.setName(SENSOR_NAME);
        sensor1.setSensorType(SENSOR_TYPE);
        sensor1.setCamera(savedCamera);

        LightSensor sensor2 = new LightSensor();
        sensor2.setName(SENSOR_NAME_2);
        sensor2.setSensorType(SENSOR_TYPE);
        sensor2.setCamera(savedCamera);

        lightSensorRepository.save(sensor1);
        lightSensorRepository.save(sensor2);

        // Act
        List<LightSensor> sensors = lightSensorRepository.findLightSensorByCamera(savedCamera);

        // Assert
        Assertions.assertThat(sensors).hasSize(2);
        Assertions.assertThat(sensors).extracting(LightSensor::getName)
                .containsExactlyInAnyOrder(SENSOR_NAME, SENSOR_NAME_2);
    }

    @Test
    public void expect_updateSensor_withValidSensor_returnsUpdatedSensor() {
        // Arrange
        LightSensor savedSensor = lightSensorRepository.save(lightSensor);
        String updatedName = "Updated Sensor Name";
        String updatedSensorVersion = "v1.1";
        String updatedSensorData = "Sensor Data";

        // Act
        savedSensor.setName(updatedName);
        savedSensor.setVersion(updatedSensorVersion);
        savedSensor.setData(updatedSensorData);
        LightSensor updatedSensor = lightSensorRepository.save(savedSensor);

        // Assert
        Assertions.assertThat(updatedSensor).isNotNull();
        Assertions.assertThat(updatedSensor.getName()).isEqualTo(updatedName);
        Assertions.assertThat(updatedSensor.getVersion()).isEqualTo(updatedSensorVersion);
        Assertions.assertThat(updatedSensor.getData()).isEqualTo(updatedSensorData);
    }
}