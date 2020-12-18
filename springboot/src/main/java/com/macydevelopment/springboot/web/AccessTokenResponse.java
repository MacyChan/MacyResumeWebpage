package com.macydevelopment.springboot.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.Getter;
//import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenResponse {

    @JsonProperty("access_token")
  //  @Getter
  //  @Setter
    private String access_token;

}
