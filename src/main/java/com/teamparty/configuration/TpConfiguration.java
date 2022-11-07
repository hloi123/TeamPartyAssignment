package com.teamparty.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

public class TpConfiguration extends Configuration {

    private String externalUrl;

    private ApiLimitConfiguration apiLimitConfiguration;

    @JsonProperty("externalUrl")
    public String getExternalUrl() {
        return externalUrl;
    }

    @JsonProperty("externalUrl")
    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    @JsonProperty("apiLimit")
    public ApiLimitConfiguration getApiLimitConfiguration() {
        return apiLimitConfiguration;
    }

    @JsonProperty("apiLimit")
    public void setApiLimitConfiguration(ApiLimitConfiguration apiLimitConfiguration) {
        this.apiLimitConfiguration = apiLimitConfiguration;
    }
}
