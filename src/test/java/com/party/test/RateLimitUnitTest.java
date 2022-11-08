package com.party.test;

import com.teamparty.component.RateLimit;
import com.teamparty.configuration.TpConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class RateLimitUnitTest {

    private TpConfiguration configuration = mock(TpConfiguration.class);
    private final RateLimit rateLimit = new RateLimit(configuration);

    @Test
    public void testApiRateLimitFunction() {
        String queryA = "queryA";
        String queryB = "queryB";
        String queryC = "queryC";
        for (int i = 1; i <= 10; i++) {
            assertFalse(rateLimit.isLimitedAccess(queryA));
            assertFalse(rateLimit.isLimitedAccess(queryB));
            assertFalse(rateLimit.isLimitedAccess(queryC));
        }
        assertTrue(rateLimit.isLimitedAccess(queryA));
        assertTrue(rateLimit.isLimitedAccess(queryB));
        assertTrue(rateLimit.isLimitedAccess(queryC));
    }
}
