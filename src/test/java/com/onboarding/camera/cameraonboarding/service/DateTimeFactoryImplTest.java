package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.service.impl.DateTimeFactoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DateTimeFactoryImplTest {

    @Mock
    private Clock clock;

    @InjectMocks
    private DateTimeFactoryImpl dateTimeFactory;

    private final String actualTime = dateTimeFactory.now().toString();
    private final String TEST_TIME_INSTANT = "2024-07-29T10:00:00Z";
    private final String TEST_ZONE_ID = "UCT";

    @Test
    public void expect_now_withValidClock_returnsLocalDateTime() {

        // arrange
        given(clock.instant()).willReturn(Instant.parse(TEST_TIME_INSTANT));
        given(clock.getZone()).willReturn(ZoneId.of(TEST_ZONE_ID));

        // assert
        assertThat(actualTime).isEqualTo(TEST_TIME_INSTANT);
    }
}