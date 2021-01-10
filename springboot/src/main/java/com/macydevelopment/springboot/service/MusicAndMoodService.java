package com.macydevelopment.springboot.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.lang.Math; 

import com.macydevelopment.springboot.model.IPWeatherInfo;
import com.macydevelopment.springboot.model.SpotifyUserModel;
import com.macydevelopment.springboot.repository.SpotifyUserRepository;
import com.macydevelopment.springboot.util.ApiInvalidRequestException;
import com.macydevelopment.springboot.util.RequestResponseLoggingInterceptor;
import com.macydevelopment.springboot.web.UserIPLocationResponse;
import com.macydevelopment.springboot.web.UserIPWeatherResponse;
import com.macydevelopment.springboot.web.dataseries;

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

    @Autowired
    private SpotifyUserRepository spotifyUserRepository;

    public SpotifyUserModel getSpotifyUserInfo(String currentCode, String clientIp) {

        SpotifyUserModel spotifyUserModel = new SpotifyUserModel();

        try {

            spotifyUserModel = getCurrentUserProfile(spotifyAccessTokenService.getAccessToken(currentCode));

            UserIPLocationResponse userIPLocationResponse = getIpLocation(clientIp);

            spotifyUserModel.setLocation(userIPLocationResponse.getCountryName());
            spotifyUserModel.setLatitude(userIPLocationResponse.getLatitude());
            spotifyUserModel.setLongitude(userIPLocationResponse.getLongitude());

            IPWeatherInfo ipWeatherInfo = getIpWeather(spotifyUserModel.getLatitude(), spotifyUserModel.getLongitude());

            spotifyUserModel.setWeather(ipWeatherInfo.weather);
            spotifyUserModel.setTempMax(ipWeatherInfo.tempMax);
            spotifyUserModel.setTempMin(ipWeatherInfo.tempMin);
            spotifyUserModel.setMoodLevel(getMoodLevel(ipWeatherInfo.weather, Integer.parseInt(ipWeatherInfo.tempMax), Integer.parseInt(ipWeatherInfo.tempMin)));
            spotifyUserModel.setAuthCode(currentCode);
            spotifyUserRepository.save(spotifyUserModel);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("getSpotifyUserInfo Error:" + e.getMessage());
        }

        return spotifyUserModel;
    }

    public SpotifyUserModel getCurrentUserProfile(String accessToken) {

        SpotifyUserModel spotifyUserModel = new SpotifyUserModel();

        try {

            SpotifyApi spotifyApi = spotifyAccessTokenService.setAccessToken(accessToken);

            GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile().build();

            User user = getCurrentUsersProfileRequest.execute();

            spotifyUserModel.setSpotifyUserId(user.getId());
            spotifyUserModel.setDisplayName(user.getDisplayName());
            spotifyUserModel.setCountry(user.getCountry().toString());

            System.out.println("Display name: " + user.getDisplayName());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("getCurrentUserProfile Error:" + e.getMessage());
        }

        return spotifyUserModel;

    }

    public UserIPLocationResponse getIpLocation(String clientIp) {

        if (clientIp.equals("0:0:0:0:0:0:0:1")) {
            clientIp = "";
        }
        ;

        String urlIpGEO = "https://freegeoip.app/json/" + clientIp;

        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
        HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.APPLICATION_JSON);
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

        // Converter for html response
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);

        ResponseEntity<UserIPWeatherResponse> resp = new ResponseEntity<UserIPWeatherResponse>(HttpStatus.OK);

        try {
            resp = restTemplate.exchange(urlWeather, HttpMethod.GET, entity, UserIPWeatherResponse.class, longitude,
                    latitude);
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

    public String getMoodLevel(String weather, Integer tempMax, Integer tempMin) {

        Double score = 0.0;

        switch (weather) {
            case "clear":
            score = score + 0.9;
                break;
            case "mcloudy":
            score = score + 0.6;
                break;
            case "cloudy":
            score = score + 0.3;
                break;
            case "rain":
            score = score + 0;
                break;
            case "snow":
            score = score - 1;
                break;
            case "ts":
            score = score - 1.5;
                break;
            case "tsrain":
            score = score - 2;
                break;
        }

        Integer tempDiff = tempMax - tempMin;

        if (tempDiff > 20){score = score - 1.5;}
        if (tempDiff > 10 && tempDiff <= 20){score = score - 0.5;}
        if (tempDiff > 5 && tempDiff <= 10){score = score + 0.2;}
        if (tempDiff < 5){score = score + 0.5;}


        if (tempMax > 30 || tempMin < 10){score = score - 1.5;}
        if ((tempMax > 20 && tempMax <=28) || (tempMin < 15 && tempMin >= 10)){score = score - 0.5;}
        if (tempMax < 28 && tempMin >= 20){score = score + 0.7;}


        Integer result = (int)(100/(1 + Math.exp(-score)));

        return result.toString();
    }
}
