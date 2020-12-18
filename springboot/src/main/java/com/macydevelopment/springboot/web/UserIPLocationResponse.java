package com.macydevelopment.springboot.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class UserIPLocationResponse {
    
    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("latitude")
    private String latitude;

    @JsonProperty("longitude")
    private String longitude;


    public String getCountryName() {
        return this.countryName;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }



}
