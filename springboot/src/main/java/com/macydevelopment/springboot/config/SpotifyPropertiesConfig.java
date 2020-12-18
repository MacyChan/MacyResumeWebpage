package com.macydevelopment.springboot.config;

//import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "spotify")
//@Data

public class SpotifyPropertiesConfig {

  @NotBlank
  private String ClientId;

  @NotBlank
  private String ClientSecret;

  @NotBlank
  private String RedirectURL;

  @NotBlank
  private String Scope;


  public void setClientId(String ClientId) {
    this.ClientId = ClientId;
  }

  public void setClientSecret(String ClientSecret) {
    this.ClientSecret = ClientSecret;
  }

  public void setRedirectURL(String RedirectURL) {
    this.RedirectURL = RedirectURL;
  }

  public void setScope(String Scope) {
    this.Scope = Scope;
  }

  public String getClientId() {
    return ClientId;
  }

  public String getClientSecret() {
    return ClientSecret;
  }

  public String getRedirectURL() {
    return RedirectURL;
  }

  public String getScope() {
    return Scope;
  }

}
