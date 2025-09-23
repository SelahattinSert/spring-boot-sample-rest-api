package com.onboarding.camera.cameraonboarding.service.impl;

import com.onboarding.camera.cameraonboarding.service.MetricsService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MetricsServiceImpl implements MetricsService {

    private final MeterRegistry meterRegistry;

    private Counter cameraOnboardingSuccessCounter;
    private Counter cameraOnboardingFailureCounter;

    private Counter cameraInitializationSuccessCounter;
    private Counter cameraInitializationFailureCounter;

    private Counter imageUploadSuccessCounter;
    private Counter imageUploadFailureCounter;

    private Counter imageDownloadSuccessCounter;
    private Counter imageDownloadFailureCounter;

    private Counter locationAddSuccessCounter;
    private Counter locationAddFailureCounter;

    private final Map<String, Counter> sensorCreateSuccessCounters = new HashMap<>();
    private final Map<String, Counter> sensorCreateFailureCounters = new HashMap<>();
    private final Map<String, Counter> sensorUpdateSuccessCounters = new HashMap<>();
    private final Map<String, Counter> sensorUpdateFailureCounters = new HashMap<>();
    private final Map<String, Counter> sensorDeleteSuccessCounters = new HashMap<>();
    private final Map<String, Counter> sensorDeleteFailureCounters = new HashMap<>();


    @PostConstruct
    public void init() {
        cameraOnboardingSuccessCounter = Counter.builder("camera.onboarding.success")
                .description("Number of successful camera onboarding operations")
                .register(meterRegistry);
        cameraOnboardingFailureCounter = Counter.builder("camera.onboarding.failure")
                .description("Number of failed camera onboarding operations")
                .register(meterRegistry);

        cameraInitializationSuccessCounter = Counter.builder("camera.initialization.success")
                .description("Number of successful camera initialization operations")
                .register(meterRegistry);
        cameraInitializationFailureCounter = Counter.builder("camera.initialization.failure")
                .description("Number of failed camera initialization operations")
                .register(meterRegistry);

        imageUploadSuccessCounter = Counter.builder("image.upload.success")
                .description("Number of successful image upload operations")
                .register(meterRegistry);
        imageUploadFailureCounter = Counter.builder("image.upload.failure")
                .description("Number of failed image upload operations")
                .register(meterRegistry);

        imageDownloadSuccessCounter = Counter.builder("image.download.success")
                .description("Number of successful image download operations")
                .register(meterRegistry);
        imageDownloadFailureCounter = Counter.builder("image.download.failure")
                .description("Number of failed image download operations")
                .register(meterRegistry);

        locationAddSuccessCounter = Counter.builder("location.add.success")
                .description("Number of successful location add operations")
                .register(meterRegistry);
        locationAddFailureCounter = Counter.builder("location.add.failure")
                .description("Number of failed location add operations")
                .register(meterRegistry);
    }

    @Override
    public void incrementCameraOnboardingSuccess() {
        cameraOnboardingSuccessCounter.increment();
    }

    @Override
    public void incrementCameraOnboardingFailure() {
        cameraOnboardingFailureCounter.increment();
    }

    @Override
    public void incrementCameraInitializationSuccess() {
        cameraInitializationSuccessCounter.increment();
    }

    @Override
    public void incrementCameraInitializationFailure() {
        cameraInitializationFailureCounter.increment();
    }

    @Override
    public void incrementImageUploadSuccess() {
        imageUploadSuccessCounter.increment();
    }

    @Override
    public void incrementImageUploadFailure() {
        imageUploadFailureCounter.increment();
    }

    @Override
    public void incrementImageDownloadSuccess() {
        imageDownloadSuccessCounter.increment();
    }

    @Override
    public void incrementImageDownloadFailure() {
        imageDownloadFailureCounter.increment();
    }

    @Override
    public void incrementLocationAddSuccess() {
        locationAddSuccessCounter.increment();
    }

    @Override
    public void incrementLocationAddFailure() {
        locationAddFailureCounter.increment();
    }

    @Override
    public void incrementSensorCreateSuccess(String sensorType) {
        sensorCreateSuccessCounters.computeIfAbsent(sensorType, k -> Counter.builder("sensor.create.success")
                .tag("sensor.type", k)
                .description("Number of successful sensor creation operations")
                .register(meterRegistry)).increment();
    }

    @Override
    public void incrementSensorCreateFailure(String sensorType) {
        sensorCreateFailureCounters.computeIfAbsent(sensorType, k -> Counter.builder("sensor.create.failure")
                .tag("sensor.type", k)
                .description("Number of failed sensor creation operations")
                .register(meterRegistry)).increment();
    }

    @Override
    public void incrementSensorUpdateSuccess(String sensorType) {
        sensorUpdateSuccessCounters.computeIfAbsent(sensorType, k -> Counter.builder("sensor.update.success")
                .tag("sensor.type", k)
                .description("Number of successful sensor update operations")
                .register(meterRegistry)).increment();
    }

    @Override
    public void incrementSensorUpdateFailure(String sensorType) {
        sensorUpdateFailureCounters.computeIfAbsent(sensorType, k -> Counter.builder("sensor.update.failure")
                .tag("sensor.type", k)
                .description("Number of failed sensor update operations")
                .register(meterRegistry)).increment();
    }

    @Override
    public void incrementSensorDeleteSuccess(String sensorType) {
        sensorDeleteSuccessCounters.computeIfAbsent(sensorType, k -> Counter.builder("sensor.delete.success")
                .tag("sensor.type", k)
                .description("Number of successful sensor deletion operations")
                .register(meterRegistry)).increment();
    }

    @Override
    public void incrementSensorDeleteFailure(String sensorType) {
        sensorDeleteFailureCounters.computeIfAbsent(sensorType, k -> Counter.builder("sensor.delete.failure")
                .tag("sensor.type", k)
                .description("Number of failed sensor deletion operations")
                .register(meterRegistry)).increment();
    }
}
