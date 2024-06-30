package com.example.camera_onboarding.service;

import com.example.camera_onboarding.dao.CameraRepository;
import com.example.camera_onboarding.entity.Camera;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CameraServiceImplTest {

    @Mock
    private CameraRepository cameraRepository;

    @InjectMocks
    private CameraServiceImpl cameraService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCameras() {
        Camera camera = new Camera("Camera1", "Location1");
        Camera camera2 = new Camera("Camera2", "Location2");

        when(cameraRepository.findAll()).thenReturn(Arrays.asList(camera, camera2));

        List<Camera> cameras = cameraService.getAllCameras();
        assertEquals(2, cameras.size());
        assertEquals("Camera1", cameras.get(0).getName());
        assertEquals("Camera2", cameras.get(1).getName());
        assertEquals("Location1", cameras.get(0).getLocation());
        assertEquals("Location2", cameras.get(1).getLocation());
    }

    @Test
    void testGetCamera() {
        Camera camera = new Camera("Camera1", "Location1");

        when(cameraRepository.findById(1)).thenReturn(Optional.of(camera));

        Camera foundCamera = cameraService.getCamera(1);
        assertNotNull(foundCamera);
        assertEquals("Camera1", foundCamera.getName());
        assertEquals("Location1", foundCamera.getLocation());
    }

    @Test
    void testSaveCamera() {
        Camera camera = new Camera("Camera1", "Location1");

        when(cameraRepository.save(camera)).thenReturn(camera);

        Camera savedCamera = cameraService.saveCamera(camera);
        assertNotNull(savedCamera);
        assertEquals("Camera1", savedCamera.getName());
        assertEquals("Location1", savedCamera.getLocation());
    }

    @Test
    void testDeleteCamera() {
        Camera camera = new Camera("Camera1", "Location1");
        camera.setId(1);

        when(cameraRepository.findById(1)).thenReturn(Optional.of(camera));

        cameraService.deleteCamera(1);
        verify(cameraRepository, times(1)).deleteById(1);
    }
}
