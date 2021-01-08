package com.macydevelopment.springboot.controller;

import com.macydevelopment.springboot.service.MusicAndMoodService;
import com.macydevelopment.springboot.service.SpotifyCreatePlaylistService;

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
    
    @Autowired
    private AudioFeaturesRepository audioFeaturesRepository;

    @RequestMapping(value = "/getSpotifyUserInfo", method = RequestMethod.GET)
    public SpotifyUserModel getSpotifyUserInfo(@RequestParam(value = "code", required = true) String currentCode ) {

        return musicAndMoodService.getSpotifyUserInfo(currentCode);
    }

    @RequestMapping(value = "/getMusicAndMoodPlaylist", method = RequestMethod.GET)
    public String getMusicAndMoodPlaylist(@RequestParam(value = "code", required = true) String currentCode ) {

        return spotifyCreatePlaylistService.getMusicAndMoodPlaylistId(currentCode);
    }

    @RequestMapping(value = "/getSubmitPlaylist", method = RequestMethod.GET)
    public CustomPlaylistOutput[] postSubmitPlaylist(@RequestParam(value = "code", required = true) String currentCode,
                                     @RequestParam(value = "playlistId", required = true) String playlistId
    ) {

        AudioFeaturesModel[] arrAudioFeaturesModels = spotifyCreatePlaylistService.generateCustomPlaylist(currentCode, playlistId);

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
                    audioFeatures.setScore((int)Double.parseDouble(customPlaylistOutput.score));
                    return audioFeaturesRepository.save(audioFeatures);
                }).orElseThrow(() -> new ResourceNotFoundException("Audio not found with id " + audioFeatureId));
    }


}
