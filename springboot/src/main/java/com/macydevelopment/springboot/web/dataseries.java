package com.macydevelopment.springboot.web;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class dataseries {

    @JsonProperty("weather")
    private String weather;

    @JsonProperty("temp2m")
    private Map<String, Object> temp2m;


    public String getWeather() {
        return this.weather;
    }

    public Map<String, Object> getTemp2m() {
        return this.temp2m;
    }


}
