package com.onboarding.camera.cameraonboarding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onboarding.camera.cameraonboarding.converter.SensorDtoConverter;
import com.onboarding.camera.cameraonboarding.dto.SensorDto;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.entity.TemperatureSensor;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import com.onboarding.camera.cameraonboarding.exception.SensorNotCreatedException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotUpdatedException;
import com.onboarding.camera.cameraonboarding.service.impl.TemperatureSensorService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.UUID;

@WebMvcTest(controllers = TemperatureSensorController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Import(SensorDtoConverter.class)
class TemperatureSensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TemperatureSensorService temperatureSensorService;

    @Autowired
    private ObjectMapper objectMapper;

    private SensorDto sensorDto;

    private TemperatureSensor sensor;

    private Camera camera;

    private final UUID SENSOR_ID = UUID.randomUUID();
    private final String SENSOR_NAME = "Temperature Sensor 1";
    private final SensorType SENSOR_TYPE = SensorType.TEMPERATURE;
    private final String SENSOR_VERSION = "v1.0";
    private final UUID CAMERA_ID = UUID.randomUUID();
    private final String CAMERA_NAME = "Camera 1";
    private final String FIRMWARE_VERSION = "v1.0.0";

    @BeforeEach
    void setUp() {
        sensorDto = new SensorDto();
        sensorDto.setName(SENSOR_NAME);
        sensorDto.setSensorType(SENSOR_TYPE);
        sensorDto.setVersion(SENSOR_VERSION);
        sensor = new TemperatureSensor();
        camera = new Camera();
        camera.setCamId(CAMERA_ID);
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);
    }

    @Test
    public void expect_handleCreateSensor_withValidSensorDto_returnCreated() throws Exception {

        // arrange
        sensor.setId(SENSOR_ID);
        sensor.setName(SENSOR_NAME);
        sensor.setSensorType(SENSOR_TYPE);
        Mockito.when(temperatureSensorService.handleCreateSensor(Mockito.eq(CAMERA_ID), ArgumentMatchers.any(TemperatureSensor.class))).thenReturn(sensor);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/camera/{cameraId}/sensor/temperature", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(SENSOR_ID.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(SENSOR_NAME)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sensorType", CoreMatchers.is(SENSOR_TYPE.toString())));

        Mockito.verify(temperatureSensorService).handleCreateSensor(Mockito.eq(CAMERA_ID), ArgumentMatchers.any(TemperatureSensor.class));
    }

    @Test
    public void expect_handleCreateSensor_withNullSensorName_returnBadRequest() throws Exception {

        // arrange
        sensorDto.setName(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/camera/{cameraId}/sensor/temperature", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(temperatureSensorService, Mockito.never()).handleCreateSensor(Mockito.eq(CAMERA_ID), ArgumentMatchers.any(TemperatureSensor.class));
    }

    @Test
    public void expect_handleCreateSensor_withNullSensorType_returnBadRequest() throws Exception {

        // arrange
        sensorDto.setSensorType(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/camera/{cameraId}/sensor/temperature", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(temperatureSensorService, Mockito.never()).handleCreateSensor(Mockito.eq(CAMERA_ID), ArgumentMatchers.any(TemperatureSensor.class));
    }

    @Test
    void expect_handleCreateTemperatureSensor_withSensorNotCreatedException_returnInternalServerError() throws Exception {

        // arrange
        Mockito.doThrow(new SensorNotCreatedException("Failed to create sensor"))
                .when(temperatureSensorService).handleCreateSensor(ArgumentMatchers.eq(CAMERA_ID), ArgumentMatchers.any(TemperatureSensor.class));

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/camera/{cameraId}/sensor/temperature", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());

        Mockito.verify(temperatureSensorService).handleCreateSensor(ArgumentMatchers.eq(CAMERA_ID), ArgumentMatchers.any(TemperatureSensor.class));
    }

    @Test
    void expect_getTemperatureSensors_withValidCameraId_returnOk() throws Exception {

        // arrange
        sensor.setId(SENSOR_ID);
        sensor.setName(SENSOR_NAME);
        sensor.setSensorType(SENSOR_TYPE);
        Mockito.when(temperatureSensorService.handleGetSensorsByCameraId(CAMERA_ID))
                .thenReturn(Collections.singletonList(sensor));

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/camera/{cameraId}/sensor/temperature", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", CoreMatchers.is(SENSOR_ID.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", CoreMatchers.is(SENSOR_NAME)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].sensorType", CoreMatchers.is(SENSOR_TYPE.toString())));

        Mockito.verify(temperatureSensorService).handleGetSensorsByCameraId(CAMERA_ID);
    }

    @Test
    void expect_getTemperatureSensors_withNullCameraId_returnNotFound() throws Exception {

        // arrange
        camera.setCamId(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/camera/{cameraId}/sensor/temperature", camera.getCamId())
                .contentType(MediaType.APPLICATION_JSON));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void expect_handleUpdateTemperatureSensor_withValidRequest_returnOk() throws Exception {

        // arrange
        sensor.setId(SENSOR_ID);
        String updatedName = "Updated Name";
        sensor.setName(updatedName);
        sensor.setSensorType(SENSOR_TYPE);
        Mockito.when(temperatureSensorService.handleUpdateSensor(Mockito.eq(CAMERA_ID), Mockito.eq(SENSOR_ID), ArgumentMatchers.any(TemperatureSensor.class)))
                .thenReturn(sensor);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/camera/{cameraId}/sensor/temperature/{sensorId}", CAMERA_ID, SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(SENSOR_ID.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(updatedName)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sensorType", CoreMatchers.is(SENSOR_TYPE.toString())));

        Mockito.verify(temperatureSensorService).handleUpdateSensor(Mockito.eq(CAMERA_ID), Mockito.eq(SENSOR_ID), ArgumentMatchers.any(TemperatureSensor.class));
    }

    @Test
    void expect_handleUpdateTemperatureSensor_withNullSensorId_returnNotFound() throws Exception {

        // arrange
        sensor.setId(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/camera/{cameraId}/sensor/temperature/{sensorId}", CAMERA_ID, sensor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(temperatureSensorService, Mockito.never()).handleUpdateSensor(Mockito.eq(CAMERA_ID), Mockito.eq(sensor.getId()), ArgumentMatchers.any(TemperatureSensor.class));
    }

    @Test
    void expect_handleUpdateTemperatureSensor_withNullCameraId_returnNotFound() throws Exception {

        // arrange
        camera.setCamId(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/camera/{cameraId}/sensor/temperature/{sensorId}", camera.getCamId(), SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(temperatureSensorService, Mockito.never()).handleUpdateSensor(Mockito.eq(camera.getCamId()), Mockito.eq(SENSOR_ID), ArgumentMatchers.any(TemperatureSensor.class));
    }

    @Test
    void expect_handleUpdateTemperatureSensor_withSensorNotUpdatedException_returnInternalServerError() throws Exception {

        // arrange
        Mockito.doThrow(new SensorNotUpdatedException("Error occurred while deleting sensor"))
                .when(temperatureSensorService).handleUpdateSensor(ArgumentMatchers.eq(CAMERA_ID), ArgumentMatchers.eq(SENSOR_ID), ArgumentMatchers.any(TemperatureSensor.class));

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/camera/{cameraId}/sensor/temperature/{sensorId}", CAMERA_ID, SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());

        Mockito.verify(temperatureSensorService).handleUpdateSensor(ArgumentMatchers.eq(CAMERA_ID), ArgumentMatchers.eq(SENSOR_ID), ArgumentMatchers.any(TemperatureSensor.class));
    }

    @Test
    void expect_handleDeleteTemperatureSensor_withValidRequest_returnNoContent() throws Exception {
        // Act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/camera/{cameraId}/sensor/temperature/{sensorId}", CAMERA_ID, SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(temperatureSensorService).handleDeleteSensor(CAMERA_ID, SENSOR_ID);
    }

    @Test
    void expect_handleDeleteTemperatureSensor_withNullSensorId_returnNotFound() throws Exception {

        // arrange
        sensor.setId(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/camera/{cameraId}/sensor/temperature/{sensorId}", CAMERA_ID, sensor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(temperatureSensorService, Mockito.never()).handleDeleteSensor(Mockito.eq(CAMERA_ID), Mockito.eq(sensor.getId()));
    }

    @Test
    void expect_handleDeleteTemperatureSensor_withNullCameraId_returnNotFound() throws Exception {

        // arrange
        camera.setCamId(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/camera/{cameraId}/sensor/temperature/{sensorId}", camera.getCamId(), SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(temperatureSensorService, Mockito.never()).handleDeleteSensor(Mockito.eq(camera.getCamId()), Mockito.eq(SENSOR_ID));
    }

    @Test
    void expect_handleDeleteTemperatureSensor_withSensorNotUpdatedException_returnInternalServerError() throws Exception {

        // arrange
        Mockito.doThrow(new SensorNotUpdatedException("Error occurred while deleting sensor"))
                .when(temperatureSensorService).handleDeleteSensor(CAMERA_ID, SENSOR_ID);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/camera/{cameraId}/sensor/temperature/{sensorId}", camera.getCamId(), SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());

        Mockito.verify(temperatureSensorService).handleDeleteSensor(ArgumentMatchers.eq(CAMERA_ID), ArgumentMatchers.eq(SENSOR_ID));
    }
}