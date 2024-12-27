package com.onboarding.camera.cameraonboarding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onboarding.camera.cameraonboarding.converter.SensorDtoConverter;
import com.onboarding.camera.cameraonboarding.dto.SensorDto;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.entity.LightSensor;
import com.onboarding.camera.cameraonboarding.enums.SensorType;
import com.onboarding.camera.cameraonboarding.exception.SensorNotCreatedException;
import com.onboarding.camera.cameraonboarding.exception.SensorNotUpdatedException;
import com.onboarding.camera.cameraonboarding.service.impl.LightSensorService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
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

@WebMvcTest(controllers = LightSensorController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Import(SensorDtoConverter.class)
class LightSensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LightSensorService lightSensorService;

    @Autowired
    private ObjectMapper objectMapper;

    private SensorDto sensorDto;

    private LightSensor sensor;

    private Camera camera;

    private final UUID SENSOR_ID = UUID.randomUUID();
    private final String SENSOR_NAME = "Light Sensor 1";
    private final SensorType SENSOR_TYPE = SensorType.LIGHT;
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
        sensor = new LightSensor();
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
        BDDMockito.given(lightSensorService.handleCreateSensor(Mockito.eq(CAMERA_ID), ArgumentMatchers.any(LightSensor.class))).willReturn(sensor);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/camera/{cameraId}/sensor/light", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(SENSOR_ID.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(SENSOR_NAME)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sensorType", CoreMatchers.is(SENSOR_TYPE.toString())));

        Mockito.verify(lightSensorService).handleCreateSensor(Mockito.eq(CAMERA_ID), ArgumentMatchers.any(LightSensor.class));
    }

    @Test
    public void expect_handleCreateSensor_withNullSensorName_returnBadRequest() throws Exception {

        // arrange
        sensorDto.setName(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/camera/{cameraId}/sensor/light", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(lightSensorService, Mockito.never()).handleCreateSensor(Mockito.eq(CAMERA_ID), ArgumentMatchers.any(LightSensor.class));
    }

    @Test
    public void expect_handleCreateSensor_withNullSensorType_returnBadRequest() throws Exception {

        // arrange
        sensorDto.setSensorType(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/camera/{cameraId}/sensor/light", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(lightSensorService, Mockito.never()).handleCreateSensor(Mockito.eq(CAMERA_ID), ArgumentMatchers.any(LightSensor.class));
    }

    @Test
    void expect_handleCreateLightSensor_withSensorNotCreatedException_returnInternalServerError() throws Exception {

        // arrange
        Mockito.doThrow(new SensorNotCreatedException("Failed to create sensor"))
                .when(lightSensorService).handleCreateSensor(ArgumentMatchers.eq(CAMERA_ID), ArgumentMatchers.any(LightSensor.class));

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/camera/{cameraId}/sensor/light", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());

        Mockito.verify(lightSensorService).handleCreateSensor(ArgumentMatchers.eq(CAMERA_ID), ArgumentMatchers.any(LightSensor.class));
    }

    @Test
    void expect_getLightSensors_withValidCameraId_returnOk() throws Exception {

        // arrange
        sensor.setId(SENSOR_ID);
        sensor.setName(SENSOR_NAME);
        sensor.setSensorType(SENSOR_TYPE);
        BDDMockito.given(lightSensorService.handleGetSensorsByCameraId(CAMERA_ID))
                .willReturn(Collections.singletonList(sensor));

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/camera/{cameraId}/sensor/light", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", CoreMatchers.is(SENSOR_ID.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", CoreMatchers.is(SENSOR_NAME)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].sensorType", CoreMatchers.is(SENSOR_TYPE.toString())));

        Mockito.verify(lightSensorService).handleGetSensorsByCameraId(CAMERA_ID);
    }

    @Test
    void expect_getLightSensors_withNullCameraId_returnNotFound() throws Exception {

        // arrange
        camera.setCamId(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/camera/{cameraId}/sensor/light", camera.getCamId())
                .contentType(MediaType.APPLICATION_JSON));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void expect_handleUpdateLightSensor_withValidRequest_returnOk() throws Exception {

        // arrange
        sensor.setId(SENSOR_ID);
        String updatedName = "Updated Name";
        sensor.setName(updatedName);
        sensor.setSensorType(SENSOR_TYPE);
        BDDMockito.given(lightSensorService.handleUpdateSensor(Mockito.eq(CAMERA_ID), Mockito.eq(SENSOR_ID), ArgumentMatchers.any(LightSensor.class)))
                .willReturn(sensor);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/camera/{cameraId}/sensor/light/{sensorId}", CAMERA_ID, SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(SENSOR_ID.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(updatedName)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sensorType", CoreMatchers.is(SENSOR_TYPE.toString())));

        Mockito.verify(lightSensorService).handleUpdateSensor(Mockito.eq(CAMERA_ID), Mockito.eq(SENSOR_ID), ArgumentMatchers.any(LightSensor.class));
    }

    @Test
    void expect_handleUpdateLightSensor_withNullSensorId_returnNotFound() throws Exception {

        // arrange
        sensor.setId(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/camera/{cameraId}/sensor/light/{sensorId}", CAMERA_ID, sensor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(lightSensorService, Mockito.never()).handleUpdateSensor(Mockito.eq(CAMERA_ID), Mockito.eq(sensor.getId()), ArgumentMatchers.any(LightSensor.class));
    }

    @Test
    void expect_handleUpdateLightSensor_withNullCameraId_returnNotFound() throws Exception {

        // arrange
        camera.setCamId(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/camera/{cameraId}/sensor/light/{sensorId}", camera.getCamId(), SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(lightSensorService, Mockito.never()).handleUpdateSensor(Mockito.eq(camera.getCamId()), Mockito.eq(SENSOR_ID), ArgumentMatchers.any(LightSensor.class));
    }

    @Test
    void expect_handleUpdateLightSensor_withSensorNotUpdatedException_returnInternalServerError() throws Exception {

        // arrange
        Mockito.doThrow(new SensorNotUpdatedException("Error occurred while deleting sensor"))
                .when(lightSensorService).handleUpdateSensor(ArgumentMatchers.eq(CAMERA_ID), ArgumentMatchers.eq(SENSOR_ID), ArgumentMatchers.any(LightSensor.class));

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/camera/{cameraId}/sensor/light/{sensorId}", CAMERA_ID, SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());

        Mockito.verify(lightSensorService).handleUpdateSensor(ArgumentMatchers.eq(CAMERA_ID), ArgumentMatchers.eq(SENSOR_ID), ArgumentMatchers.any(LightSensor.class));
    }

    @Test
    void expect_handleDeleteLightSensor_withValidRequest_returnNoContent() throws Exception {
        // Act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/camera/{cameraId}/sensor/light/{sensorId}", CAMERA_ID, SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(lightSensorService).handleDeleteSensor(CAMERA_ID, SENSOR_ID);
    }

    @Test
    void expect_handleDeleteLightSensor_withNullSensorId_returnNotFound() throws Exception {

        // arrange
        sensor.setId(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/camera/{cameraId}/sensor/light/{sensorId}", CAMERA_ID, sensor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(lightSensorService, Mockito.never()).handleDeleteSensor(Mockito.eq(CAMERA_ID), Mockito.eq(sensor.getId()));
    }

    @Test
    void expect_handleDeleteLightSensor_withNullCameraId_returnNotFound() throws Exception {

        // arrange
        camera.setCamId(null);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/camera/{cameraId}/sensor/light/{sensorId}", camera.getCamId(), SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(lightSensorService, Mockito.never()).handleDeleteSensor(Mockito.eq(camera.getCamId()), Mockito.eq(SENSOR_ID));
    }

    @Test
    void expect_handleDeleteLightSensor_withSensorNotUpdatedException_returnInternalServerError() throws Exception {

        // arrange
        Mockito.doThrow(new SensorNotUpdatedException("Error occurred while deleting sensor"))
                .when(lightSensorService).handleDeleteSensor(CAMERA_ID, SENSOR_ID);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/camera/{cameraId}/sensor/light/{sensorId}", camera.getCamId(), SENSOR_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sensorDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());

        Mockito.verify(lightSensorService).handleDeleteSensor(ArgumentMatchers.eq(CAMERA_ID), ArgumentMatchers.eq(SENSOR_ID));
    }
}