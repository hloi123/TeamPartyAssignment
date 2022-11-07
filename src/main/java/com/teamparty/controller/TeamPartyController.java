package com.teamparty.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamparty.configuration.ApiLimitConfiguration;
import com.teamparty.configuration.TpConfiguration;
import com.teamparty.dto.ErrorData;
import com.teamparty.dto.JokingDataList;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.teamparty.configuration.Constant.DEFAULT_CAPACITY;
import static com.teamparty.configuration.Constant.DEFAULT_EXTERNAL_URL;
import static com.teamparty.configuration.Constant.DEFAULT_TIME_IN_SECONDS;

public class TeamPartyController {

    private TpConfiguration configuration;
    private Map<String, Bucket> bucketMap;

    public TeamPartyController(TpConfiguration configuration) {
        this.configuration = configuration;
        bucketMap = new HashMap<>();
    }

    public Response getJokes(String query) throws Exception {
        if (isLimitedAccess(query)) {
            ErrorData errorData = new ErrorData(query + " is limited, please select another one!", Response.Status.TOO_MANY_REQUESTS.getStatusCode());
            return Response.status(Response.Status.TOO_MANY_REQUESTS).entity(errorData).type(MediaType.APPLICATION_JSON_TYPE).build();
        }
        JokingDataList jokingDataList = invokeRestClient(query);
        filterKeywordValue(query, jokingDataList);
        return Response.ok(jokingDataList).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Perform API rate Limit based on query search
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
    public void filterKeywordValue(String query, JokingDataList jokingDataList) {
        Pattern pattern = Pattern.compile("\\b" + query + "\\b");
        jokingDataList.getResult().removeIf(j ->
                j.getValue() == null || !pattern.matcher(j.getValue().toLowerCase()).find());
        jokingDataList.setTotal(jokingDataList.getResult().size());
    }

    /**
     * get Joke data from ChuckNorris api
     *
     * @param query
     * @return JokingDataList
     */
    public JokingDataList invokeRestClient(String query) throws Exception {
        Client client = ClientBuilder.newClient();
        String url = configuration.getExternalUrl() == null
                ? DEFAULT_EXTERNAL_URL + query
                : configuration.getExternalUrl() + query;
        String jsonString = client.target(url).request(MediaType.APPLICATION_JSON_TYPE).get(String.class);
        if (jsonString == null) {
            return new JokingDataList();
        }
        try {
            return new ObjectMapper().readValue(jsonString, JokingDataList.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new Exception("Failing to get jokes from ChuckNorris");
        }
    }

}
