package com.macydevelopment.springboot.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserIPWeatherResponse {

    @JsonProperty("dataseries")
    private dataseries[] dataseries;

    public dataseries[] getDataSeries() {
     return this.dataseries;
    }

}
