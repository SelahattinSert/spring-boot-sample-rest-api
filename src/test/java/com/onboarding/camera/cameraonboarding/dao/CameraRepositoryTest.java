package com.onboarding.camera.cameraonboarding.dao;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.service.impl.DateTimeFactoryImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.LocalDateTime;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CameraRepositoryTest {

    @Mock
    private DateTimeFactoryImpl dateTimeFactory;

    @Autowired
    private CameraRepository cameraRepository;

    private Camera camera;

    private final String CAMERA_NAME = "Camera 1";
    private final String FIRMWARE_VERSION = "v1.0";
    private final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 7, 29, 10, 0);

    @BeforeEach
    void setUp() {
        Mockito.when(dateTimeFactory.now()).thenReturn(CREATED_AT);

        camera = new Camera();
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);
        camera.setCreatedAt(CREATED_AT);
    }

    @Test
    public void expect_save_cameraRepository_saveAll_returnsSavedCamera() {

        // act
        Camera savedCamera = cameraRepository.save(camera);

        // assert
        Assertions.assertThat(savedCamera).isNotNull();
        Assertions.assertThat(savedCamera.getCameraName()).isEqualTo(CAMERA_NAME);
        Assertions.assertThat(savedCamera.getFirmwareVersion()).isEqualTo(FIRMWARE_VERSION);
        Assertions.assertThat(savedCamera.getCreatedAt()).isEqualTo(CREATED_AT);
    }

    @Test
    public void expect_save_cameraWithNullName_throwsException() {

        // arrange
        camera.setCameraName(null);

        // act and assert
        Assertions.assertThatThrownBy(() -> {
            cameraRepository.save(camera);
            cameraRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void expect_save_cameraWithNullFirmwareVersion_throwsException() {

        // arrange
        camera.setFirmwareVersion(null);

        // act and assert
        Assertions.assertThatThrownBy(() -> {
            cameraRepository.save(camera);
            cameraRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}