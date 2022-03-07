package com.macydevelopment.springboot.service;

import com.macydevelopment.springboot.model.AccessTokenRecord;
import com.macydevelopment.springboot.config.SpotifyPropertiesConfig;
import com.macydevelopment.springboot.util.ApiInvalidRequestException;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
//import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.stream.Collectors;

import org.apache.hc.core5.http.ParseException;

import static java.time.LocalDateTime.now;

@Service
public class SpotifyAccessTokenService {

    @Autowired
    private SpotifyPropertiesConfig spotifyPropertiesConfig;

    @Autowired
    private StartUpService startUpService;

    public URI loginMusicAndMood() {

        String strLoginURI = "";

        try {

            SpotifyApi spotifyApi = setAuthSpotifyApi();

            AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                    // .state("x4xkmn9pu3j6ukrs8n")
                    .scope(spotifyPropertiesConfig.getScope())
                    // .show_dialog(true)
                    .build();

            URI uri = authorizationCodeUriRequest.execute();

            strLoginURI = getAuthCode(uri);

            System.out.println("loginURI: " + uri.toString());

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException(e.getMessage());
        }

        URI loginURI = URI.create(strLoginURI);

        return loginURI;
    }

    private static String getAuthCode(URI uri) {
        String loginUri = "";
        // BufferedReader reader = null;

        try {
            URL url = uri.toURL();
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            // reader = new BufferedReader(new
            // InputStreamReader(connection.getInputStream()));
            // String line = null;
            // StringWriter out = new StringWriter(connection.getContentLength() > 0 ?
            // connection.getContentLength() : 2048);
            loginUri = connection.getURL().toString();
        } catch (Exception e) {
            throw new ApiInvalidRequestException(e.getMessage());
        }

        return loginUri;
    }

    public String getAccessToken(String currentCode) {

        String accessToken = "";

        try {

            if (isCodeExist(currentCode) && !isCodeExpired(currentCode)) {
                // Use existing token
                accessToken = getTokenByCode(currentCode);

            } else if (isCodeExist(currentCode) && isCodeExpired(currentCode)) {
                // Get refresh token
                String refreshToken = getRefreshTokenByCode(currentCode);
                SpotifyApi spotifyApi = setRefreshSpotifyApi(refreshToken);
                AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
                        .build();
                AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

                // Update token list
                accessToken = authorizationCodeCredentials.getAccessToken();
                refreshToken = authorizationCodeCredentials.getRefreshToken();
                Integer expiresIn = authorizationCodeCredentials.getExpiresIn();
                String scope = authorizationCodeCredentials.getScope();

                addTokenRecordToGlobalList(currentCode, accessToken, refreshToken, expiresIn, scope);

            } else {
                // Get new token
                SpotifyApi spotifyApi = setAuthSpotifyApi();
                AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(currentCode).build();
                AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

                accessToken = authorizationCodeCredentials.getAccessToken();
                String refreshToken = authorizationCodeCredentials.getRefreshToken();
                Integer expiresIn = authorizationCodeCredentials.getExpiresIn();
                String scope = authorizationCodeCredentials.getScope();

                addTokenRecordToGlobalList(currentCode, accessToken, refreshToken, expiresIn, scope);

                System.out.println("Access Token: " + authorizationCodeCredentials.getAccessToken());
                System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
            }

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException(e.getMessage());
        }

        return accessToken;
    }

    private SpotifyApi setAuthSpotifyApi() {

        String clientId = spotifyPropertiesConfig.getClientId();
        String clientSecret = spotifyPropertiesConfig.getClientSecret();
        URI redirectUri = SpotifyHttpManager.makeUri(spotifyPropertiesConfig.getRedirectURL());

        return new SpotifyApi.Builder().setClientId(clientId).setClientSecret(clientSecret).setRedirectUri(redirectUri)
                .build();

    }

    private SpotifyApi setRefreshSpotifyApi(String refreshToken) {

        String clientId = spotifyPropertiesConfig.getClientId();
        String clientSecret = spotifyPropertiesConfig.getClientSecret();

        return new SpotifyApi.Builder().setClientId(clientId).setClientSecret(clientSecret)
                .setRefreshToken(refreshToken).build();
    }

    private boolean isCodeExist(String currentCode) {
        return startUpService.listAccessTokenRecords.stream()
                .filter(accessTokenRecord -> accessTokenRecord.getCode().equals(currentCode)).findFirst().isPresent();
    }

    private boolean isCodeExpired(String currentCode) {
        return startUpService.listAccessTokenRecords.stream().findFirst()
                .filter(accessTokenRecord -> accessTokenRecord.getCode().equals(currentCode))
                .filter(accessTokenRecord -> accessTokenRecord.getExpireTime().isBefore(now())).isPresent();
    }

    private String getTokenByCode(String currentCode) {

        return startUpService.listAccessTokenRecords.stream()
                .filter(accessTokenRecord -> accessTokenRecord.code.equals(currentCode))
                .findAny()
                .orElse(null)
                .getToken();
    }

    private String getRefreshTokenByCode(String currentCode) {
        return startUpService.listAccessTokenRecords.stream()
                .filter(accessTokenRecord -> accessTokenRecord.code.equals(currentCode))
                .findAny()
                .orElse(null)
                .getRefreshToken();
    }

    private void addTokenRecordToGlobalList(String currentCode, String accessToken, String refreshToken,
            Integer expiresIn, String scope) {

        startUpService.listAccessTokenRecords
                .removeIf(accessTokenRecord -> accessTokenRecord.getCode().equals(currentCode));

        AccessTokenRecord accessTokenRecords = new AccessTokenRecord();

        accessTokenRecords.setCode(currentCode);
        accessTokenRecords.setToken(accessToken);
        accessTokenRecords.setRefreshToken(refreshToken);
        accessTokenRecords.setIssueTime(now());
        accessTokenRecords.setExpireTime(now().plusSeconds(expiresIn));
        accessTokenRecords.setScope(scope);

        startUpService.listAccessTokenRecords.add(accessTokenRecords);
        startUpService.listAccessTokenRecords
                .removeIf(accessTokenRecord -> accessTokenRecord.getExpireTime().isBefore(now().minusDays(1)));

    }

    public SpotifyApi setAccessToken(String accessToken) {
        return new SpotifyApi.Builder()
                .setAccessToken(accessToken)
                .build();
    }

    public List<AccessTokenRecord> getListAccessTokenRecords() {
        return startUpService.listAccessTokenRecords;
    }

}
