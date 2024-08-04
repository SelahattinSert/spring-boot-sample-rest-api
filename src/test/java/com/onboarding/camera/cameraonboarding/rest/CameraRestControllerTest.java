package com.onboarding.camera.cameraonboarding.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onboarding.camera.cameraonboarding.converter.CameraDtoConverter;
import com.onboarding.camera.cameraonboarding.dto.CameraDto;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.service.CameraService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.UUID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        Camera camera = new Camera();
        camera.setCamId(CAMERA_ID);
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);
        given(cameraService.handleSaveCamera(ArgumentMatchers.any(Camera.class))).willReturn(camera);

        // act
        ResultActions response = mockMvc.perform(post("/api/v1/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cameraDto)));

        // assert
        response.andExpect(status().is(201))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cameraId", CoreMatchers.is(CAMERA_ID.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cameraName", CoreMatchers.is(CAMERA_NAME)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firmwareVersion", CoreMatchers.is(FIRMWARE_VERSION)));
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
        response.andExpect(status().isBadRequest());
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
        response.andExpect(status().isBadRequest());
    }

    @Test
    public void expect_handleInitializeCamera_withValidCamera_returnOk() throws Exception {

        // arrange
        camera.setCamId(CAMERA_ID);
        Mockito.doNothing().when(cameraService).handleInitializeCamera(CAMERA_ID);

        // act
        ResultActions response = mockMvc.perform(patch("/api/v1/{camera_id}/initialize", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON));

        // assert
        response.andExpect(status().isOk());

        verify(cameraService).handleInitializeCamera(CAMERA_ID);
    }

    @Test
    public void expect_handleInitializeCamera_withNullUUID_returnBadRequest() throws Exception {

        // arrange
        camera.setCamId(null);

        // act
        ResultActions response = mockMvc.perform(patch("/api/v1/{camera_id}/initialize", camera.getCamId()))
                .andExpect(status().is4xxClientError());

        // assert
        response.andExpect(status().is4xxClientError());
    }

    @Test
    void expect_handleInitializeCamera_withInvalidUUID_returnBadRequest() throws Exception {
        // Arrange
        String invalidCameraId = "123";

        // act
        ResultActions response = mockMvc.perform(patch("/api/v1/{camera_id}/initialize", invalidCameraId))
                        .andExpect(status().is4xxClientError());

        Assertions.assertEquals(UUID.fromString(CAMERA_ID.toString()), CAMERA_ID);
        Assertions.assertThrows(IllegalArgumentException.class, () -> UUID.fromString(INVALID_UUID));

        // assert
        response.andExpect(status().is4xxClientError());
    }
}