package com.onboarding.camera.cameraonboarding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onboarding.camera.cameraonboarding.converter.CameraDtoConverter;
import com.onboarding.camera.cameraonboarding.dto.CameraDto;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.exception.CameraAlreadyInitializedException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotInitializedException;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
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

import java.util.UUID;

@WebMvcTest(controllers = CameraRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Import(CameraDtoConverter.class)
class CameraRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CameraService cameraService;

    @Autowired
    private ObjectMapper objectMapper;

    private CameraDto cameraDto;

    private Camera camera;

    private final String CAMERA_NAME = "Camera 1";
    private final String FIRMWARE_VERSION = "v1.0";
    private final UUID CAMERA_ID = UUID.randomUUID();
    private final String INVALID_UUID = "invalid-uuid";

    @BeforeEach
    void setUp() {
        cameraDto = new CameraDto();
        cameraDto.setCameraName(CAMERA_NAME);
        cameraDto.setFirmwareVersion(FIRMWARE_VERSION);
        camera = new Camera();
    }

    @Test
    public void expect_handleSaveCamera_withValidCameraDto_returnCreated() throws Exception {

        // arrange
        camera.setCamId(CAMERA_ID);
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);
        BDDMockito.given(cameraService.handleSaveCamera(ArgumentMatchers.any(Camera.class))).willReturn(camera);

        // act
        final ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cameraDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cameraId", CoreMatchers.is(CAMERA_ID.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cameraName", CoreMatchers.is(CAMERA_NAME)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firmwareVersion", CoreMatchers.is(FIRMWARE_VERSION)));

        Mockito.verify(cameraService).handleSaveCamera(ArgumentMatchers.any(Camera.class));
    }

    @Test
    public void expect_handleSaveCamera_withNullCameraName_returnBadRequest() throws Exception {

        // arrange
        cameraDto.setCameraName(null);

        // act
        final ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cameraDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(cameraService, Mockito.never()).handleSaveCamera(ArgumentMatchers.any(Camera.class));
    }

    @Test
    public void expect_handleSaveCamera_withNullFirmwareVersion_returnBadRequest() throws Exception {

        // arrange
        cameraDto.setFirmwareVersion(null);

        // act
        final ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cameraDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(cameraService, Mockito.never()).handleSaveCamera(ArgumentMatchers.any(Camera.class));
    }

    @Test
    public void expect_handleInitializeCamera_withValidCamera_returnOk() throws Exception {

        // arrange
        camera.setCamId(CAMERA_ID);
        Mockito.doNothing().when(cameraService).handleInitializeCamera(CAMERA_ID);

        // act
        final ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/{camera_id}/initialize", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(cameraService).handleInitializeCamera(CAMERA_ID);
    }

    @Test
    public void expect_handleInitializeCamera_withNullUUID_returnBadRequest() throws Exception {

        // arrange
        camera.setCamId(null);

        // act
        final ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/{camera_id}/initialize", camera.getCamId()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        // assert
        response.andExpect(MockMvcResultMatchers.status().is4xxClientError());

        Mockito.verify(cameraService, Mockito.never()).handleInitializeCamera(CAMERA_ID);
    }

    @Test
    void expect_handleInitializeCamera_withInvalidUUID_returnBadRequest() throws Exception {
        // Arrange
        final String invalidCameraId = "123";

        // act
        final ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/{camera_id}/initialize", invalidCameraId))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        Assertions.assertEquals(UUID.fromString(CAMERA_ID.toString()), CAMERA_ID);
        Assertions.assertThrows(IllegalArgumentException.class, () -> UUID.fromString(INVALID_UUID));

        // assert
        response.andExpect(MockMvcResultMatchers.status().is4xxClientError());

        Mockito.verify(cameraService, Mockito.never()).handleInitializeCamera(CAMERA_ID);
    }

    @Test
    void expect_handleInitializeCamera_withAlreadyInitializedCamera_returnConflict() throws Exception {

        // arrange
        Mockito.doThrow(new CameraAlreadyInitializedException("Camera already initialized"))
                .when(cameraService).handleInitializeCamera(CAMERA_ID);

        // act
        final ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/{camera_id}/initialize", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isConflict());

        Mockito.verify(cameraService).handleInitializeCamera(CAMERA_ID);
    }

    @Test
    public void expect_handleInitializeCamera_withNotInitializedCamera_returnInternalServerError() throws Exception {
        // arrange
        Mockito.doThrow(new CameraNotInitializedException("Error occurred while initializing camera"))
                .when(cameraService).handleInitializeCamera(CAMERA_ID);

        // act
        final ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/{camera_id}/initialize", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());

        Mockito.verify(cameraService).handleInitializeCamera(CAMERA_ID);
    }
}