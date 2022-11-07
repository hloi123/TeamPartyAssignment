package com.teamparty.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class JokingDataList {

    @JsonProperty
    private Integer total;
    @JsonProperty
    private List<JokingData> result = new ArrayList<>();

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<JokingData> getResult() {
        return result;
    }

    public void setResult(List<JokingData> result) {
        this.result = result;
    }
}
