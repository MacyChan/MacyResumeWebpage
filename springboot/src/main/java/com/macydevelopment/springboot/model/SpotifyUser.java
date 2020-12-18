package com.macydevelopment.springboot.model;

//import lombok.Data;

//@Data
public class SpotifyUser {

    public String birthdate;
    public String country;
    public String displayName;
    public String id;
    public String location;
    public String latitude;
    public String longitude;
    public String weather;
    public String tempMax;
    public String tempMin;
    public String moodLevel;



    public String getBirthdate() {
        return this.birthdate;
    }

    public String getCountry() {
        return this.country;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getId() {
        return this.id;
    }

    public String getLocation() {
        return this.location;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public String getWeather() {
        return this.weather;
    }

    public String getTempMax() {
        return this.tempMax;
    }

    public String getTempMin() {
        return this.tempMin;
    }

    public String getMoodLevel() {
        return this.moodLevel;
    }
}