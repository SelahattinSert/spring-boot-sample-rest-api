package com.onboarding.camera.cameraonboarding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onboarding.camera.cameraonboarding.converter.SensorDtoConverter;
import com.onboarding.camera.cameraonboarding.dto.SensorDto;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.entity.MotionSensor;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import com.onboarding.camera.cameraonboarding.exception.SensorNotCreatedException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotUpdatedException;
import com.onboarding.camera.cameraonboarding.service.impl.MotionSensorService;
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

@WebMvcTest(controllers = MotionSensorController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Import(SensorDtoConverter.class)
class MotionSensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MotionSensorService motionSensorService;

    @Autowired
    private ObjectMapper objectMapper;

    private SensorDto sensorDto;

    private MotionSensor sensor;

    private Camera camera;

    private final UUID SENSOR_ID = UUID.randomUUID();
    private final String SENSOR_NAME = "Motion Sensor 1";
    private final SensorType SENSOR_TYPE = SensorType.MOTION;
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
        sensor = new MotionSensor();
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
        Mockito.when(motionSensorService.handleCreateSensor(Mockito.eq(CAMERA_ID), ArgumentMatchers.any(MotionSensor.class))).thenReturn(sensor);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/camera/{cameraId}/sensor/motion", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(SENSOR_ID.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(SENSOR_NAME)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sensorType", CoreMatchers.is(SENSOR_TYPE.toString())));

        Mockito.verify(motionSensorService).handleCreateSensor(Mockito.eq(CAMERA_ID), ArgumentMatchers.any(MotionSensor.class));
    }

    @Test
    public void expect_handleCreateSensor_withNullSensorName_returnBadRequest() throws Exception {

        // arrange
        sensorDto.setName(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/camera/{cameraId}/sensor/motion", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(motionSensorService, Mockito.never()).handleCreateSensor(Mockito.eq(CAMERA_ID), ArgumentMatchers.any(MotionSensor.class));
    }

    @Test
    public void expect_handleCreateSensor_withNullSensorType_returnBadRequest() throws Exception {

        // arrange
        sensorDto.setSensorType(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/camera/{cameraId}/sensor/motion", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(motionSensorService, Mockito.never()).handleCreateSensor(Mockito.eq(CAMERA_ID), ArgumentMatchers.any(MotionSensor.class));
    }

    @Test
    void expect_handleCreateMotionSensor_withSensorNotCreatedException_returnInternalServerError() throws Exception {

        // arrange
        Mockito.doThrow(new SensorNotCreatedException("Failed to create sensor"))
                .when(motionSensorService).handleCreateSensor(ArgumentMatchers.eq(CAMERA_ID), ArgumentMatchers.any(MotionSensor.class));

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/camera/{cameraId}/sensor/motion", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());

        Mockito.verify(motionSensorService).handleCreateSensor(ArgumentMatchers.eq(CAMERA_ID), ArgumentMatchers.any(MotionSensor.class));
    }

    @Test
    void expect_getMotionSensors_withValidCameraId_returnOk() throws Exception {

        // arrange
        sensor.setId(SENSOR_ID);
        sensor.setName(SENSOR_NAME);
        sensor.setSensorType(SENSOR_TYPE);
        Mockito.when(motionSensorService.handleGetSensorsByCameraId(CAMERA_ID))
                .thenReturn(Collections.singletonList(sensor));

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/camera/{cameraId}/sensor/motion", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", CoreMatchers.is(SENSOR_ID.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", CoreMatchers.is(SENSOR_NAME)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].sensorType", CoreMatchers.is(SENSOR_TYPE.toString())));

        Mockito.verify(motionSensorService).handleGetSensorsByCameraId(CAMERA_ID);
    }

    @Test
    void expect_getMotionSensors_withNullCameraId_returnNotFound() throws Exception {

        // arrange
        camera.setCamId(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/camera/{cameraId}/sensor/motion", camera.getCamId())
                .contentType(MediaType.APPLICATION_JSON));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void expect_handleUpdateMotionSensor_withValidRequest_returnOk() throws Exception {

        // arrange
        sensor.setId(SENSOR_ID);
        String updatedName = "Updated Name";
        sensor.setName(updatedName);
        sensor.setSensorType(SENSOR_TYPE);
        Mockito.when(motionSensorService.handleUpdateSensor(Mockito.eq(CAMERA_ID), Mockito.eq(SENSOR_ID), ArgumentMatchers.any(MotionSensor.class)))
                .thenReturn(sensor);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/camera/{cameraId}/sensor/motion/{sensorId}", CAMERA_ID, SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(SENSOR_ID.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(updatedName)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sensorType", CoreMatchers.is(SENSOR_TYPE.toString())));

        Mockito.verify(motionSensorService).handleUpdateSensor(Mockito.eq(CAMERA_ID), Mockito.eq(SENSOR_ID), ArgumentMatchers.any(MotionSensor.class));
    }

    @Test
    void expect_handleUpdateMotionSensor_withNullSensorId_returnNotFound() throws Exception {

        // arrange
        sensor.setId(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/camera/{cameraId}/sensor/motion/{sensorId}", CAMERA_ID, sensor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(motionSensorService, Mockito.never()).handleUpdateSensor(Mockito.eq(CAMERA_ID), Mockito.eq(sensor.getId()), ArgumentMatchers.any(MotionSensor.class));
    }

    @Test
    void expect_handleUpdateMotionSensor_withNullCameraId_returnNotFound() throws Exception {

        // arrange
        camera.setCamId(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/camera/{cameraId}/sensor/motion/{sensorId}", camera.getCamId(), SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(motionSensorService, Mockito.never()).handleUpdateSensor(Mockito.eq(camera.getCamId()), Mockito.eq(SENSOR_ID), ArgumentMatchers.any(MotionSensor.class));
    }

    @Test
    void expect_handleUpdateMotionSensor_withSensorNotUpdatedException_returnInternalServerError() throws Exception {

        // arrange
        Mockito.doThrow(new SensorNotUpdatedException("Error occurred while deleting sensor"))
                .when(motionSensorService).handleUpdateSensor(ArgumentMatchers.eq(CAMERA_ID), ArgumentMatchers.eq(SENSOR_ID), ArgumentMatchers.any(MotionSensor.class));

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/camera/{cameraId}/sensor/motion/{sensorId}", CAMERA_ID, SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());

        Mockito.verify(motionSensorService).handleUpdateSensor(ArgumentMatchers.eq(CAMERA_ID), ArgumentMatchers.eq(SENSOR_ID), ArgumentMatchers.any(MotionSensor.class));
    }

    @Test
    void expect_handleDeleteMotionSensor_withValidRequest_returnNoContent() throws Exception {
        // Act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/camera/{cameraId}/sensor/motion/{sensorId}", CAMERA_ID, SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(motionSensorService).handleDeleteSensor(CAMERA_ID, SENSOR_ID);
    }

    @Test
    void expect_handleDeleteMotionSensor_withNullSensorId_returnNotFound() throws Exception {

        // arrange
        sensor.setId(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/camera/{cameraId}/sensor/motion/{sensorId}", CAMERA_ID, sensor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(motionSensorService, Mockito.never()).handleDeleteSensor(Mockito.eq(CAMERA_ID), Mockito.eq(sensor.getId()));
    }

    @Test
    void expect_handleDeleteMotionSensor_withNullCameraId_returnNotFound() throws Exception {

        // arrange
        camera.setCamId(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/camera/{cameraId}/sensor/motion/{sensorId}", camera.getCamId(), SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(motionSensorService, Mockito.never()).handleDeleteSensor(Mockito.eq(camera.getCamId()), Mockito.eq(SENSOR_ID));
    }

    @Test
    void expect_handleDeleteMotionSensor_withSensorNotUpdatedException_returnInternalServerError() throws Exception {

        // arrange
        Mockito.doThrow(new SensorNotUpdatedException("Error occurred while deleting sensor"))
                .when(motionSensorService).handleDeleteSensor(CAMERA_ID, SENSOR_ID);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/camera/{cameraId}/sensor/motion/{sensorId}", camera.getCamId(), SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());

        Mockito.verify(motionSensorService).handleDeleteSensor(ArgumentMatchers.eq(CAMERA_ID), ArgumentMatchers.eq(SENSOR_ID));
    }
}