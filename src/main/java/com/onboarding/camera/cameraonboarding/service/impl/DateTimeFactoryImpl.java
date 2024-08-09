package com.onboarding.camera.cameraonboarding.service.impl;

import com.onboarding.camera.cameraonboarding.service.DateTimeFactory;
import org.springframework.stereotype.Service;
import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class DateTimeFactoryImpl implements DateTimeFactory {

    private final Clock clock;

    public DateTimeFactoryImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
}
