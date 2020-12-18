package com.macydevelopment.springboot.controller;

import com.macydevelopment.springboot.service.SpotifyAccessTokenService;


//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.net.URI;



@RestController
public class AccessTokenController {


    @Autowired
    SpotifyAccessTokenService spotifyAccessTokenService;

    //private final Logger log = LoggerFactory.getLogger(this.getClass());



    @RequestMapping(value = "/loginMusicAndMood", method = RequestMethod.GET)
    public URI loginMusicAndMood() {

        return spotifyAccessTokenService.loginMusicAndMood();
    }


    @RequestMapping(value = "/getAccessToken", method = RequestMethod.GET)
    public String getAccessToken(@RequestParam(value = "code", required = true) String code) {

        return spotifyAccessTokenService.getAccessToken(code);
    }

}
