package com.onboarding.camera.cameraonboarding.service;

import com.onboarding.camera.cameraonboarding.service.impl.DateTimeFactoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DateTimeFactoryImplTest {

    @MockBean
    private Clock clock;

    @Autowired
    private DateTimeFactoryImpl dateTimeFactory;

    @Test
    public void expect_now_withValidClock_returnsLocalDateTime() {

        // arrange
        given(clock.instant()).willReturn(Instant.parse("2024-07-29T10:00:00Z"));
        given(clock.getZone()).willReturn(ZoneId.of("UCT"));

        // act
        String actualTime = dateTimeFactory.now().toString();

        // assert
        assertThat(actualTime).isEqualTo("2024-07-29T10:00");
    }

    @Test
    public void expect_now_withWrongClock_returnsLocalDateTime() {

        // arrange
        given(clock.instant()).willReturn(Instant.parse("2024-05-14T15:00:00Z"));
        given(clock.getZone()).willReturn(ZoneId.of("UCT"));

        // act
        String actualTime = dateTimeFactory.now().toString();

        // assert
        assertThat(actualTime).isNotEqualTo("2024-07-29T10:00");
    }

    @Test
    public void expect_now_withInvalidClock_throwsException() {

        // arrange
        given(clock.instant()).willThrow(new IllegalArgumentException("Invalid clock"));
        given(clock.getZone()).willReturn(ZoneId.of("UCT"));

        // act and assert
        assertThatThrownBy(() -> dateTimeFactory.now())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid clock");
    }
}