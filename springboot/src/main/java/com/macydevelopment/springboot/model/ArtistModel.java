package com.macydevelopment.springboot.model;

import javax.persistence.*;

@Entity
@Table(name = "mm_artistInfo")
public class ArtistModel extends AuditModel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "artistInfo_generator")
    @SequenceGenerator(name = "artistInfo_generator", sequenceName = "artistInfo_sequence", initialValue = 1000)
    private Long id;

    @Column(columnDefinition = "text")
    private String artistId;

    @Column(columnDefinition = "text")
    private String name;

    @Column(columnDefinition = "text")
    private String genres;

    @Column(columnDefinition = "integer")
    private Integer popularity;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArtistId() {
        return this.artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenres() {
        return this.genres;
    }

    public void setGenres(String strings) {
        this.genres = strings;
    }

    public Integer getPopularity() {
        return this.popularity;
    }

    public void setPopularity(Integer integer) {
        this.popularity = integer;
    }

}