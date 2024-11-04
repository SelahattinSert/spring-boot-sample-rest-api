package com.onboarding.camera.cameraonboarding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onboarding.camera.cameraonboarding.converter.CameraDtoConverter;
import com.onboarding.camera.cameraonboarding.converter.LocationDtoConverter;
import com.onboarding.camera.cameraonboarding.dto.CameraDto;
import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.exception.CameraAlreadyInitializedException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotFoundException;
import com.onboarding.camera.cameraonboarding.exception.CameraNotInitializedException;
import com.onboarding.camera.cameraonboarding.exception.ImageAlreadyUploadedException;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Base64;
import java.util.UUID;

@WebMvcTest(controllers = CameraRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Import({CameraDtoConverter.class, LocationDtoConverter.class})
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
    private final UUID IMAGE_ID = UUID.randomUUID();
    private final String IMAGE_DATA = "iVBORw0KGgoAAAANSUhEUgAAAAIAAAAECAYAAACk7+45AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAAXSURBVBhXY5STV/rPAARMIAIE0BkMDAAtLgFmLE0FhAAAAABJRU5ErkJggg==";

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
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/onboard")
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
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/onboard")
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
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/onboard")
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
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/{camera_id}/initialize", CAMERA_ID)
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
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/{camera_id}/initialize", camera.getCamId()))
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
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/{camera_id}/initialize", invalidCameraId))
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
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/{camera_id}/initialize", CAMERA_ID)
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
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/{camera_id}/initialize", CAMERA_ID)
                .contentType(MediaType.APPLICATION_JSON));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());

        Mockito.verify(cameraService).handleInitializeCamera(CAMERA_ID);
    }

    @Test
    public void expect_handleUploadImage_withValidImage_returnOk() throws Exception {
        // arrange
        camera.setCamId(CAMERA_ID);
        Mockito.doNothing().when(cameraService).handleUploadImage(CAMERA_ID, IMAGE_ID, Base64.getDecoder().decode(IMAGE_DATA));

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/camera/{camera_id}/upload_image", CAMERA_ID)
                .param("imageId", IMAGE_ID.toString())
                .param("data", IMAGE_DATA)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        response.andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(cameraService).handleUploadImage(CAMERA_ID, IMAGE_ID, Base64.getDecoder().decode(IMAGE_DATA));
    }

    @Test
    public void expect_handleUploadImage_withInvalidImageId_returnBadRequest() throws Exception {
        // arrange
        final String invalidImageId = "invalid-uuid";

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/camera/{camera_id}/upload_image", CAMERA_ID)
                        .param("imageId", invalidImageId)
                        .param("data", IMAGE_DATA)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        // assert
        response.andExpect(MockMvcResultMatchers.status().is4xxClientError());
        Mockito.verify(cameraService, Mockito.never()).handleUploadImage(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void expect_handleUploadImage_withBlankImageId_returnBadRequest() throws Exception {
        // arrange
        final String blankImageId = "";

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/camera/{camera_id}/upload_image", CAMERA_ID)
                        .param("imageId", blankImageId)
                        .param("data", IMAGE_DATA)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        // assert
        response.andExpect(MockMvcResultMatchers.status().is4xxClientError());
        Mockito.verify(cameraService, Mockito.never()).handleUploadImage(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void expect_handleUploadImage_withAlreadyImageUploadedCamera_returnConflict() throws Exception {
        // arrange
        camera.setImageId(IMAGE_ID);
        Mockito.doThrow(new ImageAlreadyUploadedException("Camera already have image with id: " + camera.getImageId()))
                .when(cameraService).handleUploadImage(CAMERA_ID, IMAGE_ID, Base64.getDecoder().decode(IMAGE_DATA));

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/camera/{camera_id}/upload_image", CAMERA_ID)
                .param("imageId", IMAGE_ID.toString())
                .param("data", IMAGE_DATA)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isConflict());

        Mockito.verify(cameraService).handleUploadImage(CAMERA_ID, IMAGE_ID, Base64.getDecoder().decode(IMAGE_DATA));
    }

    @Test
    public void expect_handleUploadImage_withNotInitializedCamera_returnInternalServerError() throws Exception {
        // arrange
        camera.setCamId(CAMERA_ID);
        Mockito.doThrow(new CameraNotInitializedException("Camera is not initialized with id: " + camera.getCamId()))
                .when(cameraService).handleUploadImage(CAMERA_ID, IMAGE_ID, Base64.getDecoder().decode(IMAGE_DATA));

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/camera/{camera_id}/upload_image", CAMERA_ID)
                .param("imageId", IMAGE_ID.toString())
                .param("data", IMAGE_DATA)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());

        Mockito.verify(cameraService).handleUploadImage(CAMERA_ID, IMAGE_ID, Base64.getDecoder().decode(IMAGE_DATA));
    }

    @Test
    public void expect_handleUploadImage_withNotOnboardedCamera_returnNotFound() throws Exception {
        // arrange
        camera.setCamId(CAMERA_ID);
        Mockito.doThrow(new CameraNotFoundException("Camera is not onboarded with id: " + camera.getCamId()))
                .when(cameraService).handleUploadImage(CAMERA_ID, IMAGE_ID, Base64.getDecoder().decode(IMAGE_DATA));

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/camera/{camera_id}/upload_image", CAMERA_ID)
                .param("imageId", IMAGE_ID.toString())
                .param("data", IMAGE_DATA)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(cameraService).handleUploadImage(CAMERA_ID, IMAGE_ID, Base64.getDecoder().decode(IMAGE_DATA));
    }

    @Test
    public void expect_handleDownloadImage_withValidCamera_returnOk() throws Exception {
        // arrange
        byte[] imageData = Base64.getDecoder().decode(IMAGE_DATA);
        camera.setCamId(CAMERA_ID);
        camera.setImageId(IMAGE_ID);
        Mockito.when(cameraService.handleDownloadImage(CAMERA_ID)).thenReturn(imageData);
        Mockito.when(cameraService.getCameraById(CAMERA_ID)).thenReturn(camera);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/camera/{camera_id}/download_image", CAMERA_ID)
                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + IMAGE_ID + ".png"))
                .andExpect(MockMvcResultMatchers.content().bytes(imageData));

        Mockito.verify(cameraService).handleDownloadImage(CAMERA_ID);
        Mockito.verify(cameraService).getCameraById(CAMERA_ID);
    }

    @Test
    public void expect_handleDownloadImage_withInvalidUUID_returnBadRequest() throws Exception {
        // arrange & act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/camera/{camera_id}/download_image", INVALID_UUID)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());

        // assert
        response.andExpect(MockMvcResultMatchers.status().is4xxClientError());

        Mockito.verify(cameraService, Mockito.never()).handleDownloadImage(Mockito.any());
    }

    @Test
    public void expect_handleDownloadImage_withNotFoundCamera_returnNotFound() throws Exception {
        // arrange
        Mockito.doThrow(new CameraNotFoundException("Camera not found with id: " + CAMERA_ID))
                .when(cameraService).handleDownloadImage(CAMERA_ID);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/camera/{camera_id}/download_image", CAMERA_ID)
                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(cameraService).handleDownloadImage(CAMERA_ID);
    }

    @Test
    public void expect_handleDownloadImage_withNotInitializedCamera_returnInternalServerError() throws Exception {
        // arrange
        camera.setCamId(CAMERA_ID);
        Mockito.doThrow(new CameraNotInitializedException("Camera is not initialized with id: " + camera.getCamId()))
                .when(cameraService).handleDownloadImage(CAMERA_ID);

        // act
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/camera/{camera_id}/download_image", CAMERA_ID)
                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));

        // assert
        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());

        Mockito.verify(cameraService).handleDownloadImage(CAMERA_ID);
    }
}