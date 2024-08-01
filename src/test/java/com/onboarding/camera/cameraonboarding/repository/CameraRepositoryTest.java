package com.onboarding.camera.cameraonboarding.repository;

import com.onboarding.camera.cameraonboarding.entity.Camera;
import com.onboarding.camera.cameraonboarding.exception.CameraNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CameraRepositoryTest {

    @Autowired
    private CameraRepository cameraRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    private Camera camera;

    private final String CAMERA_NAME = "Camera 1";
    private final String FIRMWARE_VERSION = "v1.0";
    private final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 7, 29, 10, 0);
    private final UUID NON_EXISTING_UUID = UUID.fromString("ef556dc0-0ddc-4f39-a96d-6886a54eee54");

    @BeforeEach
    void setUp() {
        camera = new Camera();
        camera.setCameraName(CAMERA_NAME);
        camera.setFirmwareVersion(FIRMWARE_VERSION);
        camera.setCreatedAt(CREATED_AT);
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    @Transactional
    public void expect_save_cameraRepository_saveAll_returnsSavedCamera() {

        // act
        Camera savedCamera = cameraRepository.save(camera);

        // assert
        Assertions.assertThat(savedCamera).isNotNull();
        Assertions.assertThat(savedCamera.getCameraName()).isNotNull();
        Assertions.assertThat(savedCamera.getCameraName()).isEqualTo(CAMERA_NAME);
        Assertions.assertThat(savedCamera.getFirmwareVersion()).isNotNull();
        Assertions.assertThat(savedCamera.getFirmwareVersion()).isEqualTo(FIRMWARE_VERSION);
        Assertions.assertThat(savedCamera.getCreatedAt()).isNotNull();
        Assertions.assertThat(savedCamera.getCreatedAt()).isEqualTo(CREATED_AT);
        Assertions.assertThat(savedCamera.getCamId()).isNotNull();
    }

    @Test
    @Transactional
    public void expect_save_cameraWithNullName_throwsException() {

        // arrange
        camera.setCameraName(null);

        // act and assert
        Assertions.assertThatThrownBy(() ->
                transactionTemplate.execute(status -> {
                    cameraRepository.saveAndFlush(camera);
                    return null;
                })
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @Transactional
    public void expect_save_cameraWithNullFirmwareVersion_throwsException() {

        // arrange
        camera.setFirmwareVersion(null);

        // act and assert
        Assertions.assertThatThrownBy(() ->
                transactionTemplate.execute(status -> {
                    cameraRepository.saveAndFlush(camera);
                    return null;
                })
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void expect_findById_cameraRepository_returnsCamera() {

        // arrange
        transactionTemplate.execute(status -> {
            cameraRepository.save(camera);
            return null;
        });

        // act
        Optional<Camera> foundCamera = cameraRepository.findById(camera.getCamId());

        // act and assert
        Assertions.assertThat(foundCamera).isPresent();
        Assertions.assertThat(foundCamera.get().getCamId()).isEqualTo(camera.getCamId());
    }

    @Test
    public void expect_findById_cameraWithNullCamId_throwsException() {

        // arrange
        camera.setCamId(null);

        // act and assert
        Assertions.assertThatThrownBy(() ->
                cameraRepository.findById(camera.getCamId())
        ).isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    public void expect_findById_cameraWithNotExistingCamId_throwsException() {

        // arrange
        camera.setCamId(NON_EXISTING_UUID);

        // act
        Optional<Camera> foundCamera = cameraRepository.findById(NON_EXISTING_UUID);

        // assert
        Assertions.assertThatThrownBy(() -> {
                    if (foundCamera.isEmpty()) {
                        throw new CameraNotFoundException("Camera not found with ID: " + NON_EXISTING_UUID);
                    }
                }
        ).isInstanceOf(CameraNotFoundException.class);
    }
}