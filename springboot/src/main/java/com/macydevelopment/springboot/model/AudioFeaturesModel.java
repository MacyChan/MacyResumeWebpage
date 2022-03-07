package com.macydevelopment.springboot.model;

import javax.persistence.*;

import com.wrapper.spotify.enums.Modality;
import com.wrapper.spotify.enums.ModelObjectType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "mm_audiofeatures")
public class AudioFeaturesModel extends AuditModel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "audiofeatures_generator")
    @SequenceGenerator(name = "audiofeatures_generator", sequenceName = "audiofeatures_sequence", initialValue = 1000)
    private Long id;

    @Column(columnDefinition = "text")
    private String authCode;

    @Column(columnDefinition = "text")
    private String spotifyUserId;

    @Column(columnDefinition = "text")
    private String customPlaylistId;

    @Column(columnDefinition = "text")
    private String songId;

    @Column(columnDefinition = "text")
    private String songName;

    @Column(precision = 4, scale = 3)
    private Float acousticness;

    @Column(precision = 4, scale = 3)
    private Float danceability;

    @Column(columnDefinition = "integer")
    private Integer durationMs;

    @Column(precision = 4, scale = 3)
    private Float energy;

    @Column(precision = 4, scale = 3)
    private Float instrumentalness;

    @Column(columnDefinition = "integer")
    private Integer key;

    @Column(columnDefinition = "text")
    private Float liveness;

    @Column(precision = 4, scale = 3)
    private Float loudness;

    @Column(columnDefinition = "text")
    private Modality mode;

    @Column(precision = 5, scale = 4)
    private Float speechiness;

    @Column(precision = 5, scale = 4)
    private Float tempo;

    @Column(columnDefinition = "integer")
    private Integer timeSignature;

    @Column(columnDefinition = "text")
    private ModelObjectType type;

    @Column(precision = 4, scale = 3)
    private Float valence;

    @Column(precision = 1, scale = 0)
    private Integer Score;
    /*
     * public Long getId() {
     * return this.id;
     * }
     * 
     * public void setId(Long id) {
     * this.id = id;
     * }
     * 
     * public String getAuthCode() {
     * return this.authCode;
     * }
     * 
     * public void setAuthCode(String authCode) {
     * this.authCode = authCode;
     * }
     * 
     * public String getSpotifyUserId() {
     * return this.spotifyUserId;
     * }
     * 
     * public void setSpotifyUserId(String spotifyUserId) {
     * this.spotifyUserId = spotifyUserId;
     * }
     * 
     * public String getCustomPlaylistId() {
     * return this.customPlaylistId;
     * }
     * 
     * public void setCustomPlaylistId(String customPlaylistId) {
     * this.customPlaylistId = customPlaylistId;
     * }
     * 
     * public String getSongId() {
     * return this.songId;
     * }
     * 
     * public void setSongId(String songId) {
     * this.songId = songId;
     * }
     * 
     * public String getSongName() {
     * return this.songName;
     * }
     * 
     * public void setSongName(String songName) {
     * this.songName = songName;
     * }
     * 
     * public Float getAcousticness() {
     * return this.acousticness;
     * }
     * 
     * public void setAcousticness(Float acousticness) {
     * this.acousticness = acousticness;
     * }
     * 
     * public Float getDanceability() {
     * return this.danceability;
     * }
     * 
     * public void setDanceability(Float danceability) {
     * this.danceability = danceability;
     * }
     * 
     * public Integer getDurationMs() {
     * return this.durationMs;
     * }
     * 
     * public void setDurationMs(Integer durationMs) {
     * this.durationMs = durationMs;
     * }
     * 
     * public Float getEnergy() {
     * return this.energy;
     * }
     * 
     * public void setEnergy(Float energy) {
     * this.energy = energy;
     * }
     * 
     * public Float getInstrumentalness() {
     * return this.instrumentalness;
     * }
     * 
     * public void setInstrumentalness(Float instrumentalness) {
     * this.instrumentalness = instrumentalness;
     * }
     * 
     * public Integer getKey() {
     * return this.key;
     * }
     * 
     * public void setKey(Integer key) {
     * this.key = key;
     * }
     * 
     * public Float getLiveness() {
     * return this.liveness;
     * }
     * 
     * public void setLiveness(Float liveness) {
     * this.liveness = liveness;
     * }
     * 
     * public Float getLoudness() {
     * return this.loudness;
     * }
     * 
     * public void setLoudness(Float loudness) {
     * this.loudness = loudness;
     * }
     * 
     * public Modality getMode() {
     * return this.mode;
     * }
     * 
     * public void setMode(Modality mode) {
     * this.mode = mode;
     * }
     * 
     * public Float getSpeechiness() {
     * return this.speechiness;
     * }
     * 
     * public void setSpeechiness(Float speechiness) {
     * this.speechiness = speechiness;
     * }
     * 
     * public Float getTempo() {
     * return this.tempo;
     * }
     * 
     * public void setTempo(Float tempo) {
     * this.tempo = tempo;
     * }
     * 
     * public Integer getTimeSignature() {
     * return this.timeSignature;
     * }
     * 
     * public void setTimeSignature(Integer timeSignature) {
     * this.timeSignature = timeSignature;
     * }
     * 
     * public ModelObjectType getType() {
     * return this.type;
     * }
     * 
     * public void setType(ModelObjectType type) {
     * this.type = type;
     * }
     * 
     * public Float getValence() {
     * return this.valence;
     * }
     * 
     * public void setValence(Float valence) {
     * this.valence = valence;
     * }
     * 
     * public Integer getScore() {
     * return this.Score;
     * }
     * 
     * public void setScore(Integer Score) {
     * this.Score = Score;
     * }
     */
}