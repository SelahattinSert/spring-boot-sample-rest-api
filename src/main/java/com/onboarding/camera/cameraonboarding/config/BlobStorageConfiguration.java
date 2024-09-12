package com.onboarding.camera.cameraonboarding.config;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.ParallelTransferOptions;
import com.azure.storage.common.policy.RequestRetryOptions;
import com.azure.storage.common.policy.RetryPolicyType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.time.Duration;

@Slf4j
@Data
@Configuration
public class BlobStorageConfiguration {

    @Value("${spring.cloud.azure.storage.connection-string}")
    public String azureStorageConnectionString;

    @Value("${spring.cloud.azure.storage.blob.container-name}")
    public String azureStorageContainerName;

    @Bean
    public BlobServiceAsyncClient blobServiceClientBuilder() {
        return new BlobServiceClientBuilder().connectionString(azureStorageConnectionString)
                .retryOptions(
                        new RequestRetryOptions(
                                RetryPolicyType.EXPONENTIAL,
                                5,
                                Duration.ofSeconds(300L),
                                null,
                                null,
                                null)).buildAsyncClient();
    }

    public BlobContainerAsyncClient getBlobAsyncClient(final String containerName) {
        return blobServiceClientBuilder().getBlobContainerAsyncClient(containerName);
    }

    public Flux<ByteBuffer> convertByteArrayToFlux(final byte[] byteArray) {
        return Flux.just(ByteBuffer.wrap(byteArray));
    }

    public ParallelTransferOptions getTransferOptions(final long blockSize) {
        return new ParallelTransferOptions()
                .setBlockSizeLong(blockSize)
                .setMaxConcurrency(5)
                .setProgressListener(
                        bytesTransferred -> log.info("Uploading bytes:{}", bytesTransferred));
    }
}