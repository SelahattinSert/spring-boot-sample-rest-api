package com.onboarding.camera.cameraonboarding.dao;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import org.assertj.core.api.Assertions;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE)
class CameraRepositoryTest {

    @Autowired
    private CameraRepository cameraRepository;

    private Camera camera;

    @BeforeEach
    void setUp() {
        camera = new Camera();
        camera.setCameraName("Camera 1");
        camera.setFirmwareVersion("v1.0");
    }

    @Test
    public void expect_save_cameraRepository_saveAll_returnsSavedCamera() {

        // act
        Camera savedCamera = cameraRepository.save(camera);

        // assert
        Assertions.assertThat(savedCamera).isNotNull();
        Assertions.assertThat(savedCamera.getCameraName()).isEqualTo("Camera 1");
        Assertions.assertThat(savedCamera.getFirmwareVersion()).isEqualTo("v1.0");
    }

    @Test
    public void expect_save_cameraWithNullName_throwsException() {

        // arrange
        camera.setCameraName(null);

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraRepository.save(camera))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void expect_save_cameraWithNullFirmwareVersion_throwsException() {

        // arrange
        camera.setFirmwareVersion(null);

        // act and assert
        Assertions.assertThatThrownBy(() -> cameraRepository.save(camera))
                .isInstanceOf(ConstraintViolationException.class);
    }
}