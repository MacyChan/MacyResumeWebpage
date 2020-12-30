package com.macydevelopment.springboot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macydevelopment.springboot.model.SpotifyUser;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.special.SnapshotResult;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.CreatePlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.RemoveItemsFromPlaylistRequest;
import com.wrapper.spotify.requests.data.tracks.GetAudioFeaturesForTrackRequest;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.macydevelopment.springboot.util.ApiInvalidRequestException;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SpotifyCreatePlaylistService {

    @Autowired
    SpotifyAccessTokenService spotifyAccessTokenService;

    @Autowired
    MusicAndMoodService musicAndMoodService;

    public String generateCustomPlaylist(String currentCode, String playlistId) {
        // Playlist customPlaylist;
        String customPlaylistId = "";//getMusicAndMoodPlaylistId(currentCode);
        int playlistLimit = 10;
        int customPlaylistLimit = 3;

        try {
            // string weather = "sunny";
            String accessToken = spotifyAccessTokenService.getAccessToken(currentCode);
            SpotifyUser spotifyUser = musicAndMoodService.getCurrentUserProfile(accessToken);
            PlaylistTrack[] selectedPlaylist = querySelectedPlaylist(accessToken, playlistId);
            String[] track = new String[playlistLimit];
            ObjectMapper oMapper = new ObjectMapper();
            List<AudioFeatures> audioFeatures = new ArrayList<AudioFeatures>();
            AudioFeatures[] arrAudioFeatures = new AudioFeatures[playlistLimit];
            String[] customTrack = new String[customPlaylistLimit];

            for (int i = 0; i < playlistLimit && i < selectedPlaylist.length; i++) {
                Map<String, Object> map = oMapper.convertValue(selectedPlaylist[i].getTrack(), Map.class);
                // track[i] = "spotify:track:" + map.get("id").toString();
                track[i] = map.get("id").toString();
                // audioFeatures[i] = getTrackAnalysis(accessToken, track[i]);
                audioFeatures.add(getTrackAnalysis(accessToken, track[i]));
            }
            audioFeatures.stream()
                    .sorted((Loudness1, Loudness2) -> Loudness1.getLoudness().compareTo(Loudness2.getLoudness()));
            audioFeatures.stream().limit(customPlaylistLimit);

            audioFeatures.toArray(arrAudioFeatures);

            for (int i = 0; i < customPlaylistLimit && i < audioFeatures.size(); i++) {
                customTrack[i] = "spotify:track:" + arrAudioFeatures[i].getId();
            }

            customPlaylistId = createNewPlaylist(accessToken, spotifyUser);

/*
            if (customPlaylistId.isEmpty()) {
                customPlaylistId = createNewPlaylist(accessToken, spotifyUser);
            } else {
                PlaylistTrack[] musicAndMoodTracks = querySelectedPlaylist(accessToken, customPlaylistId);
                for (int i = 0; i < musicAndMoodTracks.length; i++) {
                    Map<String, Object> map = oMapper.convertValue(musicAndMoodTracks[i].getTrack(), Map.class);
                    removeTracks(accessToken, customPlaylistId, map.get("id").toString());
                }
            }
*/
            addTracksToPlaylist(accessToken, customPlaylistId, customTrack);

        } catch (Exception e /* IOException | SpotifyWebApiException | ParseException e */) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("generateCustomPlaylist Error:" + e.getMessage());
        }

        return customPlaylistId;
    }

    public PlaylistTrack[] querySelectedPlaylist(String accessToken, String playlistId) {
        try {

            SpotifyApi spotifyApi = spotifyAccessTokenService.setAccessToken(accessToken);

            GetPlaylistRequest getPlaylistRequest = spotifyApi.getPlaylist(playlistId)
                    // .fields("description")
                    // .market(CountryCode.SE)
                    // .additionalTypes("track,episode")
                    .build();

            Playlist playlist = getPlaylistRequest.execute();

            System.out.println("Name: " + playlist.getName());

            return playlist.getTracks().getItems();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("querySelectedPlaylist Error:" + e.getMessage());
        }
    }

    public AudioFeatures getTrackAnalysis(String accessToken, String id)
            throws ParseException, SpotifyWebApiException, IOException {

        try {

            SpotifyApi spotifyApi = spotifyAccessTokenService.setAccessToken(accessToken);

            GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest = spotifyApi.getAudioFeaturesForTrack(id)
                    .build();

            AudioFeatures audioFeatures = getAudioFeaturesForTrackRequest.execute();

            System.out.println("ID: " + audioFeatures.getId());

            return audioFeatures;

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("querySelectedPlaylist Error:" + e.getMessage());
        }
    }

    public String createNewPlaylist(String accessToken, SpotifyUser spotifyUser) {

        Playlist playlist;

        try {

            SpotifyApi spotifyApi = spotifyAccessTokenService.setAccessToken(accessToken);
            String nameOfPlaylist = "Music and Mood Playlist";
            CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(spotifyUser.getId(), nameOfPlaylist)
                    // .collaborative(false)
                    // .public_(false)
                    .description(
                            "Music according to your GEO and local weather. Supported by our powerful learning Machine. Powered by MusicAndMood@macy-chan.tech")
                    .build();

            playlist = createPlaylistRequest.execute();

            System.out.println("Name: " + playlist.getName());

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("createNewPlaylist Error:" + e.getMessage());
        }

        return playlist.getId();
    }

    public void addTracksToPlaylist(String accessToken, String playlistId, String[] uris) {
        try {

            SpotifyApi spotifyApi = spotifyAccessTokenService.setAccessToken(accessToken);

            AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApi.addItemsToPlaylist(playlistId, uris)
                    .build();

            SnapshotResult snapshotResult = addItemsToPlaylistRequest.execute();

            System.out.println("SnapshotId: " + snapshotResult.getSnapshotId());

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("addTracksToPlaylist Error:" + e.getMessage());
        }
    }

    public String getMusicAndMoodPlaylistId(String currentCode) {

        String musicAndMoodPlaylistId = "";

        try {

            String accessToken = spotifyAccessTokenService.getAccessToken(currentCode);
            SpotifyApi spotifyApi = spotifyAccessTokenService.setAccessToken(accessToken);

            GetListOfCurrentUsersPlaylistsRequest getListOfCurrentUsersPlaylistsRequest = spotifyApi
                    .getListOfCurrentUsersPlaylists()
                    // .limit(10)
                    // .offset(0)
                    .build();

            Paging<PlaylistSimplified> playlistSimplifiedPaging = getListOfCurrentUsersPlaylistsRequest.execute();

            PlaylistSimplified[] playlistSimplifieds = playlistSimplifiedPaging.getItems().clone();

            for (int i = 0; i < playlistSimplifieds.length; i++) {
                if (playlistSimplifieds[i].getName().equals("Music and Mood Playlist")) {
                    musicAndMoodPlaylistId = playlistSimplifieds[0].getId();
                    break;
                }
                ;
            }

            // System.out.println("Total: " + playlistSimplifiedPaging.getTotal());

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("getMusicAndMoodPlaylistId Error:" + e.getMessage());
        }

        return musicAndMoodPlaylistId;
    }

    public void removeTracks(String accessToken, String playlistId, String strTracks) {

        try {

            SpotifyApi spotifyApi = spotifyAccessTokenService.setAccessToken(accessToken);
            JsonArray tracks = JsonParser.parseString("[{\"uri\":\"spotify:track:" + strTracks +"\"}]").getAsJsonArray();

            RemoveItemsFromPlaylistRequest removeItemsFromPlaylistRequest = spotifyApi
            .removeItemsFromPlaylist(playlistId, tracks)
            //.snapshotId("JbtmHBDBAYu3/bt8BOXKjzKx3i0b6LCa/wVjyl6qQ2Yf6nFXkbmzuEa+ZI/U1yF+")
            .build();

            SnapshotResult snapshotResult = removeItemsFromPlaylistRequest.execute();

            System.out.println("Snapshot ID: " + snapshotResult.getSnapshotId());

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("removeTracks Error:" + e.getMessage());
        }


    }

}
