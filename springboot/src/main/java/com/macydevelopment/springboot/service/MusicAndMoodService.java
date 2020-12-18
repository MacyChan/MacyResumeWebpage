package com.macydevelopment.springboot.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.macydevelopment.springboot.model.IPWeatherInfo;
import com.macydevelopment.springboot.model.SpotifyUser;
import com.macydevelopment.springboot.util.ApiInvalidRequestException;
import com.macydevelopment.springboot.util.RequestResponseLoggingInterceptor;
import com.macydevelopment.springboot.web.UserIPLocationResponse;
import com.macydevelopment.springboot.web.UserIPWeatherResponse;
import com.macydevelopment.springboot.web.dataseries;

/*
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder; */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

@Service

public class MusicAndMoodService {

    @Autowired
    SpotifyAccessTokenService spotifyAccessTokenService;
    
    /*
     * private RestTemplate restTemplate;
     * 
     * @Autowired public MusicAndMoodService(RestTemplateBuilder builder) {
     * this.restTemplate = builder.build(); }
     */
    // private final Logger log = LoggerFactory.getLogger(this.getClass());

    public SpotifyUser getSpotifyUserInfo(String currentCode) {

        SpotifyUser spotifyUser = new SpotifyUser();
        
        try {

            spotifyUser = getCurrentUserProfile(spotifyAccessTokenService.getAccessToken(currentCode));

            UserIPLocationResponse userIPLocationResponse = getIpLocation();

            spotifyUser.location = userIPLocationResponse.getCountryName();
            spotifyUser.latitude = userIPLocationResponse.getLatitude();
            spotifyUser.longitude = userIPLocationResponse.getLongitude();

            IPWeatherInfo ipWeatherInfo = getIpWeather(spotifyUser.latitude, spotifyUser.longitude);

            spotifyUser.weather = ipWeatherInfo.weather;
            spotifyUser.tempMax = ipWeatherInfo.tempMax;
            spotifyUser.tempMin = ipWeatherInfo.tempMin;
            spotifyUser.moodLevel = "20";

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("getSpotifyUserInfo Error:" + e.getMessage());
        }

        return spotifyUser;
    }



    public SpotifyUser getCurrentUserProfile(String accessToken){

        SpotifyUser spotifyUser = new SpotifyUser();

        try {

            SpotifyApi spotifyApi = spotifyAccessTokenService.setAccessToken(accessToken);

            GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile()
                .build();

            User user = getCurrentUsersProfileRequest.execute();

            spotifyUser.id = user.getId();
            spotifyUser.displayName = user.getDisplayName();
            spotifyUser.birthdate = user.getBirthdate();
            spotifyUser.country = user.getCountry().toString();

            System.out.println("Display name: " + user.getDisplayName());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("getCurrentUserProfile Error:" + e.getMessage());
        }

        return spotifyUser;

    }

    
    public UserIPLocationResponse getIpLocation() {

        String urlIpGEO = "https://freegeoip.app/json/";

        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
        HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON);    
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        // header.set("Authorization", "Bearer" + token);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        entity = restTemplate.getForEntity(urlIpGEO, String.class);
        ResponseEntity<UserIPLocationResponse> resp = new ResponseEntity<UserIPLocationResponse>(HttpStatus.OK);
        try {
            resp = restTemplate.exchange(urlIpGEO, HttpMethod.GET, entity, UserIPLocationResponse.class);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("getIpLocation Error:" + e.getMessage());
        }

        return resp.getBody();
    }

    public IPWeatherInfo getIpWeather(String latitude, String longitude) {

        dataseries[] weather = new dataseries[1];
        IPWeatherInfo ipWeatherInfo = new IPWeatherInfo();
        String urlWeather = "http://www.7timer.info/bin/civillight.php?lon={lon}&lat={lat}&ac=0&lang=en&unit=metric&output=json&tzshift=0";

        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        entity = restTemplate.getForEntity(urlWeather, String.class, longitude, latitude);

        //Converter for html response
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();        
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));        
        messageConverters.add(converter);  
        restTemplate.setMessageConverters(messageConverters); 

        ResponseEntity<UserIPWeatherResponse> resp = new ResponseEntity<UserIPWeatherResponse>(HttpStatus.OK);

        try {
            resp = restTemplate.exchange(urlWeather, HttpMethod.GET, entity, UserIPWeatherResponse.class, longitude, latitude );
            weather = resp.getBody().getDataSeries();
            ipWeatherInfo.weather = weather[0].getWeather();
            ipWeatherInfo.tempMax = weather[0].getTemp2m().get("max").toString();
            ipWeatherInfo.tempMin = weather[0].getTemp2m().get("min").toString();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("getIpWeather Error:" + e.getMessage());
        }

        return ipWeatherInfo;
    }


    
}
