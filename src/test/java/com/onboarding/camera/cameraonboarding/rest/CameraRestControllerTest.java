package com.onboarding.camera.cameraonboarding.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onboarding.camera.cameraonboarding.dto.CameraDto;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = CameraRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class CameraRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CameraService cameraService;

    @Autowired
    private ObjectMapper objectMapper;

    private CameraDto cameraDto;

    private final String CAMERA_NAME = "Camera 1";
    private final String FIRMWARE_VERSION = "v1.0";

    @BeforeEach
    void setUp() {
        cameraDto = new CameraDto();
        cameraDto.setCameraName(CAMERA_NAME);
        cameraDto.setFirmwareVersion(FIRMWARE_VERSION);
    }

    @Test
    public void expect_handleSaveCamera_withValidCameraDto_returnCreated() throws Exception {

        // arrange
        Camera camera = new Camera();
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);

        // act
        given(cameraService.handleSaveCamera(ArgumentMatchers.any(Camera.class))).willReturn(camera);

        ResultActions response = mockMvc.perform(post("/api/v1/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cameraDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cameraName", CoreMatchers.is(CAMERA_NAME)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firmwareVersion", CoreMatchers.is(FIRMWARE_VERSION)));

        // verify
        verify(cameraService).handleSaveCamera(ArgumentMatchers.any(Camera.class));
    }

    @Test
    public void expect_handleSaveCamera_withNullCameraName_returnBadRequest() throws Exception {

        // arrange
        cameraDto.setCameraName(null);

        // act
        ResultActions response = mockMvc.perform(post("/api/v1/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cameraDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());

        //verify
        verify(cameraService, never()).handleSaveCamera(ArgumentMatchers.any(Camera.class));
    }

    @Test
    public void expect_handleSaveCamera_withNullFirmwareVersion_returnBadRequest() throws Exception {

        // arrange
        cameraDto.setFirmwareVersion(null);

        // act
        ResultActions response = mockMvc.perform(post("/api/v1/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cameraDto)));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());

        //verify
        verify(cameraService, never()).handleSaveCamera(ArgumentMatchers.any(Camera.class));
    }
}