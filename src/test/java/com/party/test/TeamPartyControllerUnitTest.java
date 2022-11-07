package com.party.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamparty.configuration.TpConfiguration;
import com.teamparty.controller.TeamPartyController;
import com.teamparty.dto.JokingData;
import com.teamparty.dto.JokingDataList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.ws.rs.client.Client;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class TeamPartyControllerUnitTest {

    @Mock
    private TpConfiguration configuration = mock(TpConfiguration.class);
    private final TeamPartyController controller = new TeamPartyController(configuration);

    private JokingDataList performGetJokesDataFromChuckNorris(String query) {
        JokingDataList jokingDataList = null;
        try {
            jokingDataList = controller.invokeRestClient(query);

        } catch (Exception e) {
            fail("Test case failed with error:", e);
        }
        assertNotNull(jokingDataList);
        assertEquals(15, jokingDataList.getTotal());
        assertEquals(15, jokingDataList.getResult().size());
        assertJokingData(jokingDataList.getResult());
        return jokingDataList;
    }

    private void assertJokingData(List<JokingData> JokingDataResults) {
        for (JokingData jd : JokingDataResults) {
            assertNotNull(jd.getCategories());
            assertNotNull(jd.getCreated_at());
            assertNotNull(jd.getIcon_url());
            assertNotNull(jd.getId());
            assertNotNull(jd.getUpdated_at());
            assertNotNull(jd.getUrl());
            assertNotNull(jd.getValue());
        }
    }

    @Test
    public void testInvokeRestClient() {
        performGetJokesDataFromChuckNorris("thunder");
    }

    @Test
    public void testFilterChuckNorrisJokes_FullMatchOnly() {
        String query = "thunder";
        JokingDataList jokingDataList = performGetJokesDataFromChuckNorris(query);
        controller.filterKeywordValue(query, jokingDataList);
        assertEquals(6, jokingDataList.getTotal());
        assertEquals(6, jokingDataList.getResult().size());
    }

    @Test
    public void testApiRateLimitFunction() {
        String queryA = "queryA";
        String queryB = "queryB";
        for (int i = 1; i <= 10; i++) {
            assertFalse(controller.isLimitedAccess(queryA));
            assertFalse(controller.isLimitedAccess(queryB));
        }
        assertTrue(controller.isLimitedAccess(queryA));
        assertTrue(controller.isLimitedAccess(queryB));
    }


}
