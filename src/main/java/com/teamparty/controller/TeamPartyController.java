package com.teamparty.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamparty.configuration.ApiLimitConfiguration;
import com.teamparty.configuration.TpConfiguration;
import com.teamparty.dto.ErrorData;
import com.teamparty.dto.JokingData;
import com.teamparty.dto.JokingDataList;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.teamparty.configuration.Constant.DEFAULT_CAPACITY;
import static com.teamparty.configuration.Constant.DEFAULT_EXTERNAL_URL;
import static com.teamparty.configuration.Constant.DEFAULT_TIME_IN_SECONDS;

public class TeamPartyController {

    private TpConfiguration configuration;
    private Map<String, Bucket> bucketMap;

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamPartyController.class.getName());

    public TeamPartyController(TpConfiguration configuration) {
        this.configuration = configuration;
        bucketMap = new HashMap<>();
    }

    public Response getJokes(String query) throws Exception {
        if (isLimitedAccess(query)) {
            ErrorData errorData = new ErrorData(query + " is limited, please select another one!", Response.Status.TOO_MANY_REQUESTS.getStatusCode());
            return Response.status(Response.Status.TOO_MANY_REQUESTS).entity(errorData).type(MediaType.APPLICATION_JSON_TYPE).build();
        }
        JokingDataList jokingDataList = invokeRestClient(getUrl(query));
        filterKeywordValue(query, jokingDataList);
        return Response.ok(jokingDataList).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Perform checking API rate Limit with query search
     *
     * @param query
     * @return boolean
     */
    public boolean isLimitedAccess(String query) {
        if (bucketMap.containsKey(query)) {
            Bucket bucket = bucketMap.get(query);
            if (!bucket.tryConsume(1)) {
                bucketMap.put(query, bucket);
                return true;
            }
        } else {
            initBucketLimit(query);
        }
        return false;
    }

    /**
     * Init bucket for limited query with capacity in time window.
     *
     * @param query
     */
    private void initBucketLimit(String query) {
        ApiLimitConfiguration apiLimitConfig = configuration.getApiLimitConfiguration();
        long capacity = apiLimitConfig != null && apiLimitConfig.getCapacity() != null
                ? apiLimitConfig.getCapacity()
                : DEFAULT_CAPACITY;
        int timeInSeconds = apiLimitConfig != null && apiLimitConfig.getTimeInSeconds() != null
                ? apiLimitConfig.getTimeInSeconds()
                : DEFAULT_TIME_IN_SECONDS;
        Bandwidth limit = Bandwidth.classic(capacity, Refill.intervally(capacity, Duration.ofSeconds(timeInSeconds)));
        Bucket bucket = Bucket4j.builder().addLimit(limit).build();
        bucket.tryConsume(1);
        bucketMap.put(query, bucket);
    }

    /**
     * Return only joke data with full match of query search
     *
     * @param query
     * @param jokingDataList
     */
    public JokingDataList filterKeywordValue(String query, JokingDataList jokingDataList) {
        Pattern pattern = Pattern.compile("\\b" + query + "\\b");
        List<JokingData> filterResults = jokingDataList.getResult().stream().filter(j ->
                j.getValue() != null && pattern.matcher(j.getValue().toLowerCase()).find()).collect(Collectors.toList());
        jokingDataList.setResult(filterResults);
        jokingDataList.setTotal(filterResults.size());
        return jokingDataList;
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
