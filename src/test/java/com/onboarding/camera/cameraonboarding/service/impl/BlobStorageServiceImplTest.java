package com.onboarding.camera.cameraonboarding.service.impl;

import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.models.ParallelTransferOptions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
class BlobStorageServiceImplTest {

    @Mock
    private BlobServiceAsyncClient blobServiceAsyncClient;

    @Mock
    private BlobContainerAsyncClient blobContainerAsyncClient;

    @Mock
    private BlobAsyncClient blobAsyncClient;

    @InjectMocks
    private BlobStorageServiceImpl blobStorageService;

    @Captor
    ArgumentCaptor<Flux<ByteBuffer>> argumentCaptor;

    @Value("${spring.cloud.azure.storage.blob.container-name}")
    private String CONTAINER_NAME;
    private final String BLOB_NAME = "test_blob";
    private final byte[] DATA = new byte[1024 * 1024];
    private final byte[] EMPTY_DATA = new byte[0];
    private final byte[] LARGE_DATA = new byte[1024 * 1024 * 50];
    private final String WRONG_CONTAINER_NAME = "wrong_container_name";

    @BeforeEach
    void setUp() {
        Mockito.when(blobServiceAsyncClient.getBlobContainerAsyncClient(CONTAINER_NAME))
                .thenReturn(blobContainerAsyncClient);
        Mockito.when(blobContainerAsyncClient.getBlobAsyncClient(BLOB_NAME))
                .thenReturn(blobAsyncClient);
    }

    @Test
    void expect_uploadFile_withValidData_returnVoid() {
        // arrange
        Mockito.when(blobAsyncClient.upload(Mockito.any(Flux.class), Mockito.any(ParallelTransferOptions.class), Mockito.eq(true)))
                .thenReturn(Mono.empty());

        Flux<ByteBuffer> expectedDataBuffer = Flux.just(ByteBuffer.wrap(DATA));

        // act
        blobStorageService.uploadFile(CONTAINER_NAME, BLOB_NAME, DATA);

        // assert
        Mockito.verify(blobAsyncClient).upload(argumentCaptor.capture(), Mockito.any(ParallelTransferOptions.class), Mockito.eq(true));
        Assertions.assertThat(argumentCaptor.getValue().collectList().block())
                .isEqualTo(expectedDataBuffer.collectList().block());
    }

    @Test
    void expect_uploadFile_withEmptyData_throwIllegalArgumentException() {
        // arrange
        Mockito.when(blobAsyncClient.upload(Mockito.any(Flux.class), Mockito.any(ParallelTransferOptions.class), Mockito.eq(true)))
                .thenThrow(new IllegalArgumentException("File is empty"));

        // act and assert
        Assertions.assertThatThrownBy(() -> blobStorageService.uploadFile(CONTAINER_NAME, BLOB_NAME, EMPTY_DATA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("File is empty");
    }

    @Test
    void expect_uploadFile_withLargeData_throwRuntimeException() {
        // arrange
        Mockito.when(blobAsyncClient.upload(Mockito.any(Flux.class), Mockito.any(ParallelTransferOptions.class), Mockito.eq(true)))
                .thenThrow(new RuntimeException("File too large"));

        // act and assert
        Assertions.assertThatThrownBy(() -> blobStorageService.uploadFile(CONTAINER_NAME, BLOB_NAME, LARGE_DATA))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("File too large");
    }

    @Test
    public void expect_uploadFile_withWrongContainerName_throwInternalServerErrorException() {
        // arrange
        Mockito.when(blobServiceAsyncClient.getBlobContainerAsyncClient(WRONG_CONTAINER_NAME))
                .thenReturn(blobContainerAsyncClient);
        Mockito.when(blobContainerAsyncClient.getBlobAsyncClient(BLOB_NAME))
                .thenReturn(blobAsyncClient);

        Mockito.when(blobAsyncClient.upload(Mockito.any(Flux.class), Mockito.any(ParallelTransferOptions.class), Mockito.eq(true)))
                .thenThrow(new InternalError("Blob container not found"));

        // act and assert
        Assertions.assertThatThrownBy(() -> blobStorageService.uploadFile(WRONG_CONTAINER_NAME, BLOB_NAME, DATA))
                .isInstanceOf(InternalError.class)
                .hasMessageContaining("Blob container not found");
    }
}