package com.party.test;

import com.teamparty.component.RateLimit;
import com.teamparty.component.TeamParty;
import com.teamparty.configuration.TpConfiguration;
import com.teamparty.dto.JokingData;
import com.teamparty.dto.JokingDataList;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.client.Client;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TeamPartyUnitTest {

    private TpConfiguration configuration = mock(TpConfiguration.class);
    private RateLimit rateLimit = mock(RateLimit.class);

    private final TeamParty controller = new TeamParty(configuration);

    private JokingDataList performGetJokesDataFromChuckNorris(String query) {
        JokingDataList jokingDataList = null;
        try {
            jokingDataList = controller.invokeRestClient(controller.getUrl(query));
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
        List<JokingData> jokingDatas = controller.filterKeywordValue(query, jokingDataList);
        assertEquals(6, jokingDatas.size());
    }


    @Test
    public void testInvokeRestClient_UrlNull() throws Exception {
        Client client = mock(Client.class);
        when(client.target(Mockito.any(String.class))).thenThrow(NullPointerException.class);
        try {
            controller.invokeRestClient(null);
            fail("Exception expected here!");
        } catch (Exception e) {
            assertEquals("Failing to call external client with query", e.getMessage());
        }
    }

}
