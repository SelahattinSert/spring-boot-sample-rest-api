package com.onboarding.camera.cameraonboarding.service;

import java.io.OutputStream;

public interface BlobStorageService {

    /**
     * this method is used for upload files to the azure blob storage
     *
     * @param container the container
     * @param blobName  the blob name
     * @param data      the data
     */
    void uploadFile(String container, String blobName, byte[] data);

    /**
     * this method is used get container name
     *
     * @return container name
     */
    String getContainerName();

    /**
     * this method is used to download blob
     *
     * @param outputStream the output stream
     * @param container    the container name
     * @param blobName     the blob name
     */
    void getBlob(OutputStream outputStream, String container, String blobName);
}
