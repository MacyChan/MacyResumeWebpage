package com.macydevelopment.springboot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class CustomPlaylistOutput {

    public String audioFeatureId;

    public String customPlaylistId;

    public String songName;

    public String score;
    /*
     * public String getAudioFeatureId() {
     * return this.audioFeatureId;
     * }
     * 
     * public void setAudioFeatureId(String audioFeatureId) {
     * this.audioFeatureId = audioFeatureId;
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
     * public String getSongName() {
     * return this.songName;
     * }
     * 
     * public void setSongName(String songName) {
     * this.songName = songName;
     * }
     * 
     * public String getScore() {
     * return this.score;
     * }
     * 
     * public void setScore(String score) {
     * this.score = score;
     * }
     */
}