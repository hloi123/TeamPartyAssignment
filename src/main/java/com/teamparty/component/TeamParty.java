package com.teamparty.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamparty.configuration.TpConfiguration;
import com.teamparty.dto.ErrorData;
import com.teamparty.dto.JokingData;
import com.teamparty.dto.JokingDataList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.teamparty.configuration.Constant.DEFAULT_EXTERNAL_URL;

public class TeamParty {

    private TpConfiguration configuration;
    private RateLimit rateLimit;

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamParty.class.getName());

    public TeamParty(TpConfiguration configuration) {
        this.configuration = configuration;
        rateLimit = new RateLimit(configuration);
    }

    public Response getJokes(String query) throws Exception {
        if (rateLimit.isLimitedAccess(query)) {
            ErrorData errorData = new ErrorData(query + " is limited, please select another one!", Response.Status.TOO_MANY_REQUESTS.getStatusCode());
            return Response.status(Response.Status.TOO_MANY_REQUESTS).entity(errorData).type(MediaType.APPLICATION_JSON_TYPE).build();
        }
        JokingDataList jokingDataList = invokeRestClient(getUrl(query));
        filterKeywordValue(query, jokingDataList);
        return Response.ok(jokingDataList).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Return only joke data with full match of query search
     *
     * @param query
     * @param jokingDataList
     */
    public List<JokingData> filterKeywordValue(String query, JokingDataList jokingDataList) {
        Pattern pattern = Pattern.compile("\\b" + query + "\\b");
        return jokingDataList.getResult().stream().filter(j ->
                j.getValue() != null && pattern.matcher(j.getValue().toLowerCase()).find()).collect(Collectors.toList());
    }

    /**
     * get Joke data from ChuckNorris api
     *
     * @param url
     * @return JokingDataList
     */
    public JokingDataList invokeRestClient(String url) throws Exception {
        try {
            LOGGER.info("Calling External client");
            Client client = ClientBuilder.newClient();
            String jsonString = client.target(url).request(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);
            return new ObjectMapper().readValue(jsonString, JokingDataList.class);
        } catch (Exception e) {
            LOGGER.error("Failing to call external client with query = {}", url, e);
            throw new Exception("Failing to call external client with query", e);
        }
    }

    public String getUrl(String query) {
        return configuration.getExternalUrl() == null
                ? DEFAULT_EXTERNAL_URL + query
                : configuration.getExternalUrl() + query;
    }

}
