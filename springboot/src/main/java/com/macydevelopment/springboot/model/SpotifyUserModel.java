package com.macydevelopment.springboot.model;

//import lombok.Data;

//@Data

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "mm_spotifyUsers")
public class SpotifyUserModel extends AuditModel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "spotifyUsers_generator")
    @SequenceGenerator(name = "spotifyUsers_generator", sequenceName = "spotifyUsers_sequence", initialValue = 1000)
    private Long id;

    @Column(columnDefinition = "text")
    private String authCode;

    @Column(columnDefinition = "text")
    private String spotifyUserId;

    @Column(columnDefinition = "text")
    private String displayName;

    @Column(columnDefinition = "text")
    private String country;

    @Column(columnDefinition = "text")
    private String location;

    @Column(columnDefinition = "text")
    private String latitude;

    @Column(columnDefinition = "text")
    private String longitude;

    @Column(columnDefinition = "text")
    private String weather;

    @Column(columnDefinition = "text")
    private String tempMax;

    @Column(columnDefinition = "text")
    private String tempMin;

    @Column(columnDefinition = "text")
    private String moodLevel;

    /*
     * public String getAuthCode() {
     * return this.authCode;
     * }
     * 
     * public void setAuthCode(String authCode) {
     * this.authCode = authCode;
     * }
     * 
     * public String getDisplayName() {
     * return this.displayName;
     * }
     * 
     * public void setDisplayName(String displayName) {
     * this.displayName = displayName;
     * }
     * 
     * public String getSpotifyUserId() {
     * return this.spotifyUserId;
     * }
     * 
     * public void setSpotifyUserId(String spotifyUserId) {
     * this.spotifyUserId = spotifyUserId;
     * }
     * 
     * public String getCountry() {
     * return this.country;
     * }
     * 
     * public void setCountry(String country) {
     * this.country = country;
     * }
     * 
     * public String getLocation() {
     * return this.location;
     * }
     * 
     * public void setLocation(String location) {
     * this.location = location;
     * }
     * 
     * public String getLatitude() {
     * return this.latitude;
     * }
     * 
     * public void setLatitude(String latitude) {
     * this.latitude = latitude;
     * }
     * 
     * public String getLongitude() {
     * return this.longitude;
     * }
     * 
     * public void setLongitude(String longitude) {
     * this.longitude = longitude;
     * }
     * 
     * public String getWeather() {
     * return this.weather;
     * }
     * 
     * public void setWeather(String weather) {
     * this.weather = weather;
     * }
     * 
     * public String getTempMax() {
     * return this.tempMax;
     * }
     * 
     * public void setTempMax(String tempMax) {
     * this.tempMax = tempMax;
     * }
     * 
     * public String getTempMin() {
     * return this.tempMin;
     * }
     * 
     * public void setTempMin(String tempMin) {
     * this.tempMin = tempMin;
     * }
     * 
     * public String getMoodLevel() {
     * return this.moodLevel;
     * }
     * 
     * public void setMoodLevel(String moodLevel) {
     * this.moodLevel = moodLevel;
     * }
     */

}
