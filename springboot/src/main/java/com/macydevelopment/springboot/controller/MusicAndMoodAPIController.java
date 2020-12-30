package com.macydevelopment.springboot.controller;

import com.macydevelopment.springboot.service.MusicAndMoodService;
import com.macydevelopment.springboot.service.SpotifyCreatePlaylistService;
import com.macydevelopment.springboot.model.SpotifyUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MusicAndMoodAPIController {

    @Autowired
    MusicAndMoodService musicAndMoodService;

    @Autowired
    SpotifyCreatePlaylistService spotifyCreatePlaylistService;

    @RequestMapping(value = "/getSpotifyUserInfo", method = RequestMethod.GET)
    public SpotifyUser getSpotifyUserInfo(@RequestParam(value = "code", required = true) String currentCode ) {

        return musicAndMoodService.getSpotifyUserInfo(currentCode);
    }

    @RequestMapping(value = "/getMusicAndMoodPlaylist", method = RequestMethod.GET)
    public String getMusicAndMoodPlaylist(@RequestParam(value = "code", required = true) String currentCode ) {

        return spotifyCreatePlaylistService.getMusicAndMoodPlaylistId(currentCode);
    }

    @RequestMapping(value = "/getSubmitPlaylist", method = RequestMethod.GET)
    public String postSubmitPlaylist(@RequestParam(value = "code", required = true) String currentCode,
                                     @RequestParam(value = "playlistId", required = true) String playlistId
    ) {
        return spotifyCreatePlaylistService.generateCustomPlaylist(currentCode, playlistId);
    }

}
