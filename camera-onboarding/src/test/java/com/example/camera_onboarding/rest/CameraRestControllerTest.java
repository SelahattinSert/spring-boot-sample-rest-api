package com.example.camera_onboarding.rest;

import com.example.camera_onboarding.entity.Camera;
import com.example.camera_onboarding.service.CameraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Arrays;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CameraRestControllerTest {

    @Mock
    private CameraService cameraService;

    @InjectMocks
    private CameraRestController cameraRestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cameraRestController).build();
    }

    @Test
    void testGetAllCameras() throws Exception {
        Camera camera1 = new Camera("Camera1", "Location1");
        Camera camera2 = new Camera("Camera2", "Location2");

        when(cameraService.getAllCameras()).thenReturn(Arrays.asList(camera1, camera2));

        mockMvc.perform(get("/api/cameras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Camera1")))
                .andExpect(jsonPath("$[1].name", is("Camera2")));
    }

    @Test
    void testGetCamera() throws Exception {
        Camera camera = new Camera("Camera1", "Location1");

        when(cameraService.getCamera(1)).thenReturn(camera);

        mockMvc.perform(get("/api/cameras/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Camera1")));
    }

    @Test
    void testCreateCamera() throws Exception {
        Camera camera = new Camera("Camera1", "Location1");

        when(cameraService.saveCamera(any(Camera.class))).thenReturn(camera);

        mockMvc.perform(post("/api/cameras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Camera1\", \"location\": \"Location1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Camera1")));
    }

    @Test
    void testModifyCamera() throws Exception {
        Camera camera = new Camera("Camera1", "Location1");
        camera.setId(1);

        when(cameraService.getCamera(1)).thenReturn(camera);
        when(cameraService.saveCamera(any(Camera.class))).thenReturn(camera);

        mockMvc.perform(put("/api/cameras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"Camera1\", \"location\": \"Location1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Camera1")));
    }

    @Test
    void testDeleteCamera() throws Exception {
        Camera camera = new Camera("Camera1", "Location1");
        camera.setId(1);

        when(cameraService.getCamera(1)).thenReturn(camera);

        mockMvc.perform(delete("/api/cameras/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Camera deleted successfully: Camera1"));

        verify(cameraService, times(1)).deleteCamera(1);
    }
}
