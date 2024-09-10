package com.onboarding.camera.cameraonboarding.service.impl;

import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.onboarding.camera.cameraonboarding.config.BlobStorageConfiguration;
import com.onboarding.camera.cameraonboarding.service.BlobStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BlobStorageServiceImpl implements BlobStorageService {

    private final BlobStorageConfiguration blobStorageConfiguration;

    public BlobStorageServiceImpl(final BlobStorageConfiguration blobStorageConfiguration) {
        this.blobStorageConfiguration = blobStorageConfiguration;
    }

    @Override
    public void uploadFile(final String container, final String blobName, final byte[] data) {
        final BlobContainerAsyncClient blobContainerAsyncClient = blobStorageConfiguration.getBlobAsyncClient(container);
        final BlobAsyncClient blobAsyncClient = blobContainerAsyncClient.getBlobAsyncClient(blobName);

        if (data.length == 0) {
            throw new IllegalArgumentException("File is empty");
        }

        final long blockSize = 2L * 1024L * 1024L; // 2MB block size

        blobAsyncClient.upload(blobStorageConfiguration.convertByteArrayToFlux(data),
                        blobStorageConfiguration.getTransferOptions(blockSize), true)
                .doOnSuccess(blockBlobItem -> log.info("File successfully uploaded"))
                .doOnError(throwable -> log.error("Error while uploading file", throwable))
                .subscribe();
    }

    @Override
    public String getContainerName() {
        return blobStorageConfiguration.getAzureStorageContainerName();
    }
}
