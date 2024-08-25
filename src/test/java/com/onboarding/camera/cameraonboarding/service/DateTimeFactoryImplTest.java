package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.util.impl.DateTimeFactoryImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@ExtendWith(MockitoExtension.class)
class DateTimeFactoryImplTest {

    @Mock
    private Clock clock;

    @InjectMocks
    private DateTimeFactoryImpl dateTimeFactory;

    private final String TEST_TIME_INSTANT = "2024-07-29T10:00:00Z";
    private final String EXPECTED_TIME_INSTANT = "2024-07-29T10:00";
    private final String TEST_ZONE_ID = "UTC";

    @Test
    public void expect_now_withValidClock_returnsLocalDateTime() {

        // arrange
        BDDMockito.given(clock.instant()).willReturn(Instant.parse(TEST_TIME_INSTANT));
        BDDMockito.given(clock.getZone()).willReturn(ZoneId.of(TEST_ZONE_ID));

        // act
        final LocalDateTime now = dateTimeFactory.now();

        // assert
        Assertions.assertThat(now).isEqualTo(EXPECTED_TIME_INSTANT);

        Mockito.verify(clock).instant();
        Mockito.verify(clock).getZone();
    }
}
