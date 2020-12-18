package com.macydevelopment.springboot.model;

//import lombok.Data;

import java.time.LocalDateTime;


//@Data
public class AccessTokenRecord {

    public String code;
    public String token;
    public String refreshToken;
    public LocalDateTime issueTime;
    public LocalDateTime expireTime;
    public String scope;

    public String getCode() {
        return code;
    }
    
    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public LocalDateTime getIssueTime() {
        return issueTime;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public String getScope() {
        return scope;
    }
    
    public void setCode(String code) {
        this.code = code;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setIssueTime(LocalDateTime issueTime) {
        this.issueTime = issueTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

}
