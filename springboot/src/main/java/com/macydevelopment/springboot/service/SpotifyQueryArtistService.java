package com.macydevelopment.springboot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macydevelopment.springboot.model.ArtistModel;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchArtistsRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import com.macydevelopment.springboot.util.ApiInvalidRequestException;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.macydevelopment.springboot.repository.ArtistRepository;

import java.io.IOException;
import java.util.Map;

@Service
public class SpotifyQueryArtistService {

    @Autowired
    SpotifyAccessTokenService spotifyAccessTokenService;

    @Autowired
    MusicAndMoodService musicAndMoodService;

    @Autowired
    private ArtistRepository artistRepository;

    public void getArtistInfo(String accessToken, String trackId) {

        try {
            // String accessToken = spotifyAccessTokenService.getAccessToken(currentCode);
            ArtistSimplified[] artistSimplifieds = queryTrack(accessToken, trackId);

            ObjectMapper oMapper = new ObjectMapper();
            for (int i = 0; i < artistSimplifieds.length; i++) {
                Map<String, Object> map = oMapper.convertValue(artistSimplifieds[i], Map.class);
                String artistId = map.get("id").toString();
                queryArtist(accessToken, artistId);
            }

        } catch (Exception e /* IOException | SpotifyWebApiException | ParseException e */) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("getArtistInfo Error:" + e.getMessage());
        }

        // return artistModel;
    }

    public ArtistSimplified[] queryTrack(String accessToken, String trackId) {
        try {

            SpotifyApi spotifyApi = spotifyAccessTokenService.setAccessToken(accessToken);

            GetTrackRequest getTrackRequest = spotifyApi.getTrack(trackId)
                    // .market(CountryCode.SE)
                    .build();

            Track track = getTrackRequest.execute();

            // System.out.println("Artist genres: " + track.getArtists());

            return track.getArtists();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("queryArtist Error:" + e.getMessage());
        }
    }

    public Boolean queryArtist(String accessToken, String artistId) {
        try {

            SpotifyApi spotifyApi = spotifyAccessTokenService.setAccessToken(accessToken);

            GetArtistRequest getArtistRequest = spotifyApi.getArtist(artistId)
                    .build();
            Artist artist = getArtistRequest.execute();

            String[] arrGenres = artist.getGenres();
            // Write artist to database

            for (int i = 0; i < arrGenres.length; i++) {
                ArtistModel artistModel = new ArtistModel();
                artistModel.setArtistId(artist.getId());
                artistModel.setName(artist.getName());
                artistModel.setGenres(arrGenres[i]);
                artistModel.setPopularity(artist.getPopularity());
                artistRepository.save(artistModel);
            }

            return true;

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("queryArtist Error:" + e.getMessage());
        }
    }

    // Temp for ML
    public Boolean getArtistInfobyName(String currentCode, String Name) {

        try {
            String accessToken = spotifyAccessTokenService.getAccessToken(currentCode);

            SpotifyApi spotifyApi = spotifyAccessTokenService.setAccessToken(accessToken);
            SearchArtistsRequest searchArtistsRequest = spotifyApi.searchArtists(Name)
                    // .market(CountryCode.SE)
                    .limit(1)
                    // .offset(0)
                    // .includeExternal("audio")
                    .build();

            Paging<Artist> artistPaging = searchArtistsRequest.execute();

            Artist artist = artistPaging.getItems()[0];
            String[] arrGenres = artist.getGenres();

            // Write artist to database
            for (int i = 0; i < arrGenres.length; i++) {
                ArtistModel artistModel = new ArtistModel();
                artistModel.setArtistId(artist.getId());
                artistModel.setName(artist.getName());
                artistModel.setGenres(arrGenres[i]);
                artistModel.setPopularity(artist.getPopularity());
                artistRepository.save(artistModel);
            }

        } catch (Exception e /* IOException | SpotifyWebApiException | ParseException e */) {
            System.out.println("Error: " + e.getMessage());
            throw new ApiInvalidRequestException("getArtistInfo Error:" + e.getMessage());
        }

        return true;
    }

}
