package com.macydevelopment.springboot.repository;

import com.macydevelopment.springboot.model.ArtistModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<ArtistModel, Long> {
}