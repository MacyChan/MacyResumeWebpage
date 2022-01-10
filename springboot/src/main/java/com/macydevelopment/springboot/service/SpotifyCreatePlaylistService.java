package com.macydevelopment.springboot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macydevelopment.springboot.model.AudioFeaturesModel;
import com.macydevelopment.springboot.model.SpotifyUserModel;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.special.SnapshotResult;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.CreatePlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import com.wrapper.spotify.requests.data.tracks.GetAudioFeaturesForTrackRequest;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.macydevelopment.springboot.util.ApiInvalidRequestException;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.macydevelopment.springboot.repository.AudioFeaturesRepository;

import java.io.IOException;
import java.util.Map;

@Service
public class SpotifyCreatePlaylistService {

    @Autowired
    SpotifyAccessTokenService spotifyAccessTokenService;

    @Autowired
    MusicAndMoodService musicAndMoodService;

    @Autowired
    private AudioFeaturesRepository audioFeaturesRepository;

    @Autowired
    SpotifyQueryArtistService spotifyQueryArtistService;

    public AudioFeaturesModel[] generateCustomPlaylist(String currentCode, String playlistId) {
        // Playlist customPlaylist;
        String customPlaylistId = "";// getMusicAndMoodPlaylistId(currentCode);
        int playlistLimit = 30;
        int customPlaylistLimit = 10;
        AudioFeaturesModel[] arrAudioFeaturesModel = new AudioFeaturesModel[playlistLimit];

        try {
            // string weather = "sunny";
            String[] track = new String[playlistLimit];
            ObjectMapper oMapper = new ObjectMapper();
            // List<AudioFeatures> audioFeatures = new ArrayList<AudioFeatures>();
            AudioFeatures[] arrAudioFeatures = new AudioFeatures[playlistLimit];
            String[] customTrack = new String[customPlaylistLimit];

            String accessToken = spotifyAccessTokenService.getAccessToken(currentCode);
            SpotifyUserModel spotifyUser = musicAndMoodService.getCurrentUserProfile(accessToken);
            PlaylistTrack[] selectedPlaylist = querySelectedPlaylist(accessToken, playlistId);

            customPlaylistId = createNewPlaylist(accessToken, spotifyUser);

            for (int i = 0; i < playlistLimit && i < selectedPlaylist.length; i++) {
                Map<String, Object> map = oMapper.convertValue(selectedPlaylist[i].getTrack(), Map.class);
                // track[i] = "spotify:track:" + map.get("id").toString();
                track[i] = map.get("id").toString();

                // audioFeatures.add(getTrackAnalysis(accessToken, track[i]));
                arrAudioFeatures[i] = getTrackAnalysis(accessToken, track[i]);
                arrAudioFeaturesModel[i] = new AudioFeaturesModel();
                arrAudioFeaturesModel[i].setAuthCode(currentCode);
                arrAudioFeaturesModel[i].setCustomPlaylistId(customPlaylistId);
                arrAudioFeaturesModel[i].setSongName(map.get("name").toString());
                arrAudioFeaturesModel[i].setSpotifyUserId(spotifyUser.getSpotifyUserId());
                arrAudioFeaturesModel[i].setAcousticness(arrAudioFeatures[i].getAcousticness());
                arrAudioFeaturesModel[i].setDanceability(arrAudioFeatures[i].getDanceability());
                arrAudioFeaturesModel[i].setDurationMs(arrAudioFeatures[i].getDurationMs());
                arrAudioFeaturesModel[i].setEnergy(arrAudioFeatures[i].getEnergy());
                arrAudioFeaturesModel[i].setInstrumentalness(arrAudioFeatures[i].getInstrumentalness());
                arrAudioFeaturesModel[i].setKey(arrAudioFeatures[i].getKey());
                arrAudioFeaturesModel[i].setLiveness(arrAudioFeatures[i].getLiveness());
                arrAudioFeaturesModel[i].setLoudness(arrAudioFeatures[i].getLoudness());
                arrAudioFeaturesModel[i].setMode(arrAudioFeatures[i].getMode());
                arrAudioFeaturesModel[i].setSongId(arrAudioFeatures[i].getId());
                arrAudioFeaturesModel[i].setSpeechiness(arrAudioFeatures[i].getSpeechiness());
                arrAudioFeaturesModel[i].setTempo(arrAudioFeatures[i].getTempo());
                arrAudioFeaturesModel[i].setTimeSignature(arrAudioFeatures[i].getTimeSignature());
                arrAudioFeaturesModel[i].setType(arrAudioFeatures[i].getType());
                arrAudioFeaturesModel[i].setValence(arrAudioFeatures[i].getValence());
                audioFeaturesRepository.save(arrAudioFeaturesModel[i]);
            }

            // Logic on db.save & output (get Id -> customTrack[i])
            /*
             * audioFeatures.stream()
             * .sorted((Loudness1, Loudness2) ->
             * Loudness1.getLoudness().compareTo(Loudness2.getLoudness()));
             * audioFeatures.stream().limit(customPlaylistLimit);
             * 
             * audioFeatures.toArray(arrAudioFeatures);
             */

            for (int i = 0; i < customPlaylistLimit /* && i < audioFeatures.size() */; i++) {
                customTrack[i] = "spotify:track:" + arrAudioFeatures[i].getId();
            }

            /*
             * if (customPlaylistId.isEmpty()) {
             * customPlaylistId = createNewPlaylist(accessToken, spotifyUser);
             * } else {
             * PlaylistTrack[] musicAndMoodTracks = querySelectedPlaylist(accessToken,
             * customPlaylistId);
             * for (int i = 0; i < musicAndMoodTracks.length; i++) {
             * Map<String, Object> map =
             * oMapper.convertValue(musicAndMoodTracks[i].getTrack(), Map.class);
             * removeTracks(accessToken, customPlaylistId, map.get("id").toString());
             * }
             * }
             */
            addTracksToPlaylist(accessToken, customPlaylistId, customTrack);

        } catch (Exception e /* IOException | SpotifyWebApiException | ParseException e */) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("generateCustomPlaylist Error:" + e.getMessage());
        }

        return arrAudioFeaturesModel;
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

            spotifyQueryArtistService.getArtistInfo(accessToken, id);

            return audioFeatures;

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("querySelectedPlaylist Error:" + e.getMessage());
        }
    }

    public String createNewPlaylist(String accessToken, SpotifyUserModel spotifyUser) {

        Playlist playlist;

        try {

            SpotifyApi spotifyApi = spotifyAccessTokenService.setAccessToken(accessToken);
            String nameOfPlaylist = "Music and Mood Playlist";
            CreatePlaylistRequest createPlaylistRequest = spotifyApi
                    .createPlaylist(spotifyUser.getSpotifyUserId(), nameOfPlaylist)
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
    /*
     * public void removeTracks(String accessToken, String playlistId, String
     * strTracks) {
     * 
     * try {
     * 
     * SpotifyApi spotifyApi =
     * spotifyAccessTokenService.setAccessToken(accessToken);
     * JsonArray tracks = JsonParser.parseString("[{\"uri\":\"spotify:track:" +
     * strTracks +"\"}]").getAsJsonArray();
     * 
     * RemoveItemsFromPlaylistRequest removeItemsFromPlaylistRequest = spotifyApi
     * .removeItemsFromPlaylist(playlistId, tracks)
     * //.snapshotId(
     * "JbtmHBDBAYu3/bt8BOXKjzKx3i0b6LCa/wVjyl6qQ2Yf6nFXkbmzuEa+ZI/U1yF+")
     * .build();
     * 
     * SnapshotResult snapshotResult = removeItemsFromPlaylistRequest.execute();
     * 
     * System.out.println("Snapshot ID: " + snapshotResult.getSnapshotId());
     * 
     * } catch (IOException | SpotifyWebApiException | ParseException e) {
     * System.out.println("Error: " + e.getMessage());
     * throw new ApiInvalidRequestException("removeTracks Error:" + e.getMessage());
     * }
     * 
     * 
     * }
     */
}
