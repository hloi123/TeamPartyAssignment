package com.teamparty.component;

import com.teamparty.configuration.ApiLimitConfiguration;
import com.teamparty.configuration.TpConfiguration;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.teamparty.configuration.Constant.DEFAULT_CAPACITY;
import static com.teamparty.configuration.Constant.DEFAULT_TIME_IN_SECONDS;

public class RateLimit {

    private final Map<String, Bucket> bucketMap;
    private final TpConfiguration configuration;

    public RateLimit(TpConfiguration configuration) {
        this.configuration = configuration;
        bucketMap = new HashMap<>();
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
    public void initBucketLimit(String query) {
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

}
