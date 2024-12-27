package com.onboarding.camera.cameraonboarding.service.impl;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.entity.LightSensor;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import com.onboarding.camera.cameraonboarding.exception.CameraNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotCreatedException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotUpdatedException;
import com.onboarding.camera.cameraonboarding.repository.LightSensorRepository;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class LightSensorServiceTest {

    @Mock
    private LightSensorRepository lightSensorRepository;

    @Mock
    private CameraService cameraService;

    @Mock
    private Camera camera;

    @InjectMocks
    private LightSensorService lightSensorService;

    private LightSensor sensor;


    private final UUID SENSOR_ID = UUID.randomUUID();
    private final String SENSOR_NAME = "Light Sensor 1";
    private final SensorType SENSOR_TYPE = SensorType.LIGHT;
    private final UUID CAMERA_ID = UUID.randomUUID();
    private final UUID NON_EXISTING_CAM_ID = UUID.fromString("ef556dc0-0ddc-4f39-a96d-6886a54eee54");

    @BeforeEach
    void setUp() {
        sensor = new LightSensor();
        sensor.setId(SENSOR_ID);
        sensor.setName(SENSOR_NAME);
        sensor.setSensorType(SENSOR_TYPE);
    }

    @Test
    @Transactional
    void expect_handleCreateSensor_withValidSensor_returnSavedSensor() {

        // arrange
        Mockito.when(lightSensorRepository.save(sensor)).thenReturn(sensor);

        // act
        LightSensor savedSensor = lightSensorService.handleCreateSensor(CAMERA_ID, sensor);

        // assert
        Assertions.assertThat(savedSensor)
                .isNotNull()
                .satisfies(sensor -> {
                    Assertions.assertThat(sensor.getName())
                            .as("Check Sensor Name")
                            .isNotNull()
                            .isEqualTo(SENSOR_NAME);

                    Assertions.assertThat(sensor.getSensorType())
                            .as("Check Sensor Type")
                            .isNotNull()
                            .isEqualTo(SENSOR_TYPE);
                });

        Mockito.verify(lightSensorRepository).save(sensor);
    }

    @Test
    void expect_handleCreateSensor_withNonExistingCamera_throwsCameraNotFoundException() {

        // arrange
        camera.setCamId(NON_EXISTING_CAM_ID);
        Mockito.when(lightSensorRepository.save(sensor)).thenThrow(new CameraNotFoundException("Camera not found, cameraId:" + NON_EXISTING_CAM_ID));

        // act and assert
        Assertions.assertThatThrownBy(() -> lightSensorService.handleCreateSensor(NON_EXISTING_CAM_ID, sensor))
                .isInstanceOf(CameraNotFoundException.class);

        Mockito.verify(lightSensorRepository).save(sensor);
    }

    @Test
    void expect_handleCreateSensor_withNullSensorName_throwsException() {

        // arrange
        sensor.setName(null);
        Mockito.when(lightSensorRepository.save(sensor)).thenThrow(new SensorNotCreatedException("Sensor name cannot be null"));

        // act and assert
        Assertions.assertThatThrownBy(() -> lightSensorService.handleCreateSensor(CAMERA_ID, sensor))
                .isInstanceOf(SensorNotCreatedException.class);

        Mockito.verify(lightSensorRepository).save(sensor);
    }

    @Test
    void expect_handleCreateSensor_withNullSensorType_throwsException() {

        // arrange
        sensor.setSensorType(null);
        Mockito.when(lightSensorRepository.save(sensor)).thenThrow(new SensorNotCreatedException("Sensor type cannot be null"));

        // act and assert
        Assertions.assertThatThrownBy(() -> lightSensorService.handleCreateSensor(CAMERA_ID, sensor))
                .isInstanceOf(SensorNotCreatedException.class);

        Mockito.verify(lightSensorRepository).save(sensor);
    }

    @Test
    void expect_handleUpdateSensor_withValidData_returnsUpdatedSensor() {
        // arrange
        sensor.setCamera(camera);
        sensor.setName("Updated Name");
        sensor.setVersion("v1.1");
        sensor.setData("Updated Data");
        Mockito.when(cameraService.getCameraById(CAMERA_ID)).thenReturn(camera);
        Mockito.when(lightSensorRepository.save(sensor)).thenReturn(sensor);
        Mockito.when(camera.getSensors()).thenReturn(new ArrayList<>(List.of(sensor)));

        // act
        LightSensor updatedSensor = lightSensorService.handleUpdateSensor(CAMERA_ID, SENSOR_ID, sensor);

        // assert
        Assertions.assertThat(updatedSensor)
                .isNotNull()
                .satisfies(sensor -> {
                    Assertions.assertThat(sensor.getName())
                            .as("Check Sensor Name")
                            .isNotNull()
                            .isEqualTo("Updated Name");

                    Assertions.assertThat(sensor.getVersion())
                            .as("Check Sensor Version")
                            .isNotNull()
                            .isEqualTo("v1.1");

                    Assertions.assertThat(sensor.getCamera())
                            .as("Check Sensor Camera")
                            .isNotNull()
                            .isEqualTo(camera);

                    Assertions.assertThat(sensor.getData())
                            .as("Check Sensor Data")
                            .isNotNull()
                            .isEqualTo("Updated Data");
                });

        Mockito.verify(lightSensorRepository).save(sensor);
    }

    @Test
    void expect_handleUpdateSensor_withNonExistingSensor_throwsSensorNotFoundException() {

        // arrange
        Mockito.when(cameraService.getCameraById(CAMERA_ID)).thenReturn(camera);
        Mockito.when(camera.getSensors()).thenReturn(List.of());

        // act and assert
        Assertions.assertThatThrownBy(() -> lightSensorService.handleUpdateSensor(CAMERA_ID, SENSOR_ID, sensor))
                .isInstanceOf(SensorNotFoundException.class)
                .hasMessageContaining("Sensor not found");

        Mockito.verify(lightSensorRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void expect_handleUpdateSensor_withNonExistingCamera_throwsCameraNotFoundException() {
        // arrange
        Mockito.when(cameraService.getCameraById(CAMERA_ID)).thenThrow(new CameraNotFoundException("Camera not found, cameraId:" + CAMERA_ID));

        // act and assert
        Assertions.assertThatThrownBy(() -> lightSensorService.handleUpdateSensor(CAMERA_ID, SENSOR_ID, sensor))
                .isInstanceOf(CameraNotFoundException.class)
                .hasMessageContaining("Camera not found");

        Mockito.verify(lightSensorRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void expect_handleUpdateSensor_withRepositoryError_throwsSensorNotUpdatedException() {

        // arrange
        Mockito.when(cameraService.getCameraById(CAMERA_ID)).thenReturn(camera);
        Mockito.when(camera.getSensors()).thenReturn(new ArrayList<>(List.of(sensor)));
        Mockito.when(lightSensorRepository.save(sensor))
                .thenThrow(new SensorNotUpdatedException("Error occurred while updating sensors"));

        // act and assert
        Assertions.assertThatThrownBy(() -> lightSensorService.handleUpdateSensor(CAMERA_ID, SENSOR_ID, sensor))
                .isInstanceOf(SensorNotUpdatedException.class)
                .hasMessageContaining("Error occurred while updating sensors");

        Mockito.verify(lightSensorRepository).save(sensor);
    }

    @Test
    void expect_getSensorById_withValidSensorId_returnsSensor() {

        // arrange
        Mockito.when(lightSensorRepository.findById(SENSOR_ID)).thenReturn(java.util.Optional.of(sensor));

        // act
        LightSensor foundSensor = lightSensorService.getSensorById(SENSOR_ID);

        // assert
        Assertions.assertThat(foundSensor).isEqualTo(sensor);
        Mockito.verify(lightSensorRepository).findById(SENSOR_ID);
    }

    @Test
    void expect_getSensorById_withInvalidSensorId_throwsSensorNotFoundException() {

        // arrange
        Mockito.when(lightSensorRepository.findById(SENSOR_ID)).thenReturn(java.util.Optional.empty());

        // act and assert
        Assertions.assertThatThrownBy(() -> lightSensorService.getSensorById(SENSOR_ID))
                .isInstanceOf(SensorNotFoundException.class);

        Mockito.verify(lightSensorRepository).findById(SENSOR_ID);
    }

    @Test
    void expect_handleDeleteSensor_withValidData_deletesSensor() {

        // arrange
        Mockito.when(cameraService.getCameraById(CAMERA_ID)).thenReturn(camera);
        Mockito.when(camera.getSensors()).thenReturn(new ArrayList<>(List.of(sensor)));

        // act
        lightSensorService.handleDeleteSensor(CAMERA_ID, SENSOR_ID);

        // assert
        Mockito.verify(lightSensorRepository).delete(sensor);
    }

    @Test
    void expect_handleDeleteSensor_withNonExistingSensor_throwsSensorNotFoundException() {

        // arrange
        Mockito.when(cameraService.getCameraById(CAMERA_ID)).thenReturn(camera);
        Mockito.when(camera.getSensors()).thenReturn(List.of());

        // act and assert
        Assertions.assertThatThrownBy(() -> lightSensorService.handleDeleteSensor(CAMERA_ID, SENSOR_ID))
                .isInstanceOf(SensorNotFoundException.class)
                .hasMessageContaining("Sensor not found with id");

        Mockito.verify(lightSensorRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    void expect_handleDeleteSensor_withNonExistingCamera_throwsCameraNotFoundException() {

        // arrange
        Mockito.when(cameraService.getCameraById(CAMERA_ID))
                .thenThrow(new CameraNotFoundException("Camera not found, cameraId:" + CAMERA_ID));

        // act and assert
        Assertions.assertThatThrownBy(() -> lightSensorService.handleDeleteSensor(CAMERA_ID, SENSOR_ID))
                .isInstanceOf(CameraNotFoundException.class)
                .hasMessageContaining("Camera not found, cameraId");

        Mockito.verify(lightSensorRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    void expect_handleDeleteSensor_withRepositoryError_throwsSensorNotUpdatedException() {

        // arrange
        Mockito.when(cameraService.getCameraById(CAMERA_ID)).thenReturn(camera);
        Mockito.when(camera.getSensors()).thenReturn(new ArrayList<>(List.of(sensor)));
        Mockito.doThrow(new RuntimeException("Error occurred while deleting sensor"))
                .when(lightSensorRepository).delete(sensor);

        // act and assert
        Assertions.assertThatThrownBy(() -> lightSensorService.handleDeleteSensor(CAMERA_ID, SENSOR_ID))
                .isInstanceOf(SensorNotUpdatedException.class)
                .hasMessageContaining("Error occurred while deleting sensor");

        Mockito.verify(lightSensorRepository).delete(sensor);
    }
}