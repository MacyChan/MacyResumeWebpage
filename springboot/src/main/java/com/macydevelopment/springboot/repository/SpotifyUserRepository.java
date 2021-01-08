package com.macydevelopment.springboot.repository;

import com.macydevelopment.springboot.model.SpotifyUserModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotifyUserRepository extends JpaRepository<SpotifyUserModel, Long> {
}