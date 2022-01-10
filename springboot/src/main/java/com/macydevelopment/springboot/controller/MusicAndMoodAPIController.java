package com.macydevelopment.springboot.controller;

import com.macydevelopment.springboot.service.MusicAndMoodService;
import com.macydevelopment.springboot.service.SpotifyCreatePlaylistService;
import com.macydevelopment.springboot.service.SpotifyQueryArtistService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.macydevelopment.springboot.exception.ResourceNotFoundException;
import com.macydevelopment.springboot.model.AudioFeaturesModel;
import com.macydevelopment.springboot.model.CustomPlaylistOutput;
import com.macydevelopment.springboot.model.SpotifyUserModel;
import com.macydevelopment.springboot.repository.AudioFeaturesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MusicAndMoodAPIController {

    @Autowired
    MusicAndMoodService musicAndMoodService;

    @Autowired
    SpotifyCreatePlaylistService spotifyCreatePlaylistService;

    // Temp for testing
    @Autowired
    SpotifyQueryArtistService spotifyQueryArtistService;

    @Autowired
    private AudioFeaturesRepository audioFeaturesRepository;

    @RequestMapping(value = "/getClientIp", method = RequestMethod.GET)
    public @ResponseBody String processData(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        System.out.println("ClientIp is " + clientIp);
        return clientIp;
    }

    @RequestMapping(value = "/getSpotifyUserInfo", method = RequestMethod.GET)
    public SpotifyUserModel getSpotifyUserInfo(@RequestParam(value = "code", required = true) String currentCode,
            @RequestParam(value = "clientIp", required = true) String clientIp) {

        return musicAndMoodService.getSpotifyUserInfo(currentCode, clientIp);
    }

    @RequestMapping(value = "/getMusicAndMoodPlaylist", method = RequestMethod.GET)
    public String getMusicAndMoodPlaylist(@RequestParam(value = "code", required = true) String currentCode) {

        return spotifyCreatePlaylistService.getMusicAndMoodPlaylistId(currentCode);
    }

    @RequestMapping(value = "/getSubmitPlaylist", method = RequestMethod.GET)
    public CustomPlaylistOutput[] postSubmitPlaylist(@RequestParam(value = "code", required = true) String currentCode,
            @RequestParam(value = "playlistId", required = true) String playlistId) {

        AudioFeaturesModel[] arrAudioFeaturesModels = spotifyCreatePlaylistService.generateCustomPlaylist(currentCode,
                playlistId);

        CustomPlaylistOutput[] arrCustomPlaylistOutput = new CustomPlaylistOutput[arrAudioFeaturesModels.length];

        for (int i = 0; i < arrAudioFeaturesModels.length; i++) {
            arrCustomPlaylistOutput[i] = new CustomPlaylistOutput();
            arrCustomPlaylistOutput[i].customPlaylistId = arrAudioFeaturesModels[i].getCustomPlaylistId();
            arrCustomPlaylistOutput[i].audioFeatureId = arrAudioFeaturesModels[i].getId().toString();
            arrCustomPlaylistOutput[i].songName = arrAudioFeaturesModels[i].getSongName();
        }

        return arrCustomPlaylistOutput;
    }

    @PutMapping("/db/audioFeatures/{audioFeatureId}")
    public AudioFeaturesModel updateScore(@PathVariable Long audioFeatureId,
            @Valid @RequestBody CustomPlaylistOutput customPlaylistOutput) {

        return audioFeaturesRepository.findById(audioFeatureId)
                .map(audioFeatures -> {
                    audioFeatures.setScore((int) Double.parseDouble(customPlaylistOutput.score));
                    return audioFeaturesRepository.save(audioFeatures);
                }).orElseThrow(() -> new ResourceNotFoundException("Audio not found with id " + audioFeatureId));
    }

    // Temp for testing
    /*
     * @RequestMapping(value = "/test/writeArtist", method = RequestMethod.GET)
     * public void writeArtist(@RequestParam(value = "code", required = true) String
     * currentCode,
     * 
     * @RequestParam(value = "trackId", required = true) String trackId) {
     * 
     * // spotifyQueryArtistService.getArtistInfo(currentCode, trackId);
     * spotifyQueryArtistService.getArtistInfobyName(currentCode, trackId); //
     * trackId = Name
     * }
     */
}
