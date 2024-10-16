package com.onboarding.camera.cameraonboarding.service.impl;

import com.azure.core.util.Context;
import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.DownloadRetryOptions;
import com.azure.storage.blob.models.ParallelTransferOptions;
import com.onboarding.camera.cameraonboarding.service.BlobStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.OutputStream;
import java.nio.ByteBuffer;

@Slf4j
@RequiredArgsConstructor
@Service
public class BlobStorageServiceImpl implements BlobStorageService {

    final long blockSize = 2L * 1024L * 1024L; // 2MB block size

    @Value("${spring.cloud.azure.storage.blob.container-name}")
    public String azureStorageContainerName;

    private final BlobServiceAsyncClient blobServiceAsyncClient;

    private final BlobServiceClient blobServiceClient;

    @Override

    public void uploadFile(String container, String blobName, byte[] data) {
        BlobContainerAsyncClient blobContainerAsyncClient = blobServiceAsyncClient.getBlobContainerAsyncClient(container);
        BlobAsyncClient blobAsyncClient = blobContainerAsyncClient.getBlobAsyncClient(blobName);

        if (data.length == 0) {
            throw new IllegalArgumentException("File is empty");
        }

        blobAsyncClient.upload(convertByteArrayToFlux(data),
                        getTransferOptions(blockSize), true)
                .doOnSuccess(blockBlobItem -> log.info("File successfully uploaded"))
                .doOnError(throwable -> log.error("Error while uploading file", throwable))
                .doFinally(signalType -> log.info("Finalizing upload process: {}", signalType))
                .subscribe(result -> log.info("Upload completed successfully"),
                        error -> log.error("Upload failed", error),
                        () -> log.info("Upload process completed"));
    }

    @Override
    public String getContainerName() {
        return azureStorageContainerName;
    }

    @Override
    public void getBlob(OutputStream outputStream, String container, String blobName) {
        BlobClient blobClient = getBlobClient(container, blobName);

        blobClient.downloadStreamWithResponse(outputStream, null,
                new DownloadRetryOptions().setMaxRetryRequests(5),
                null, false, null, Context.NONE);
    }

    private BlobContainerClient getBlobContainerClient(String container) {
        return blobServiceClient.getBlobContainerClient(container);
    }

    private BlobClient getBlobClient(String container, String blobName) {
        BlobContainerClient blobContainerClient = getBlobContainerClient(container);
        return blobContainerClient.getBlobClient(blobName);
    }

    /**
     * Converts a byte array into a Flux of ByteBuffer.
     *
     * @param byteArray the byte array to convert
     * @return a Flux containing ByteBuffer
     */
    private Flux<ByteBuffer> convertByteArrayToFlux(byte[] byteArray) {
        return Flux.just(ByteBuffer.wrap(byteArray));
    }

    /**
     * Returns transfer options for uploading files to blob storage.
     *
     * @param transferBlockSize the block size for the transfer
     * @return configured ParallelTransferOptions
     */
    private ParallelTransferOptions getTransferOptions(long transferBlockSize) {
        return new ParallelTransferOptions()
                .setBlockSizeLong(transferBlockSize)
                .setMaxConcurrency(5)
                .setProgressListener(
                        bytesTransferred -> log.info("Uploading bytes:{}", bytesTransferred));
    }
}
