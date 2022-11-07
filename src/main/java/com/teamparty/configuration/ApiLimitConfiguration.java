package com.teamparty.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiLimitConfiguration {
    private Long capacity;
    private Integer timeInSeconds;

    public ApiLimitConfiguration() {
    }

    public ApiLimitConfiguration(long capacity, int timeInSeconds) {
        this.capacity = capacity;
        this.timeInSeconds = timeInSeconds;
    }

    @JsonProperty("capacity")
    public Long getCapacity() {
        return capacity;
    }

    @JsonProperty("capacity")
    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    @JsonProperty("timeInSeconds")
    public Integer getTimeInSeconds() {
        return timeInSeconds;
    }

    @JsonProperty("timeInSeconds")
    public void setTimeInSeconds(Integer timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }
}
