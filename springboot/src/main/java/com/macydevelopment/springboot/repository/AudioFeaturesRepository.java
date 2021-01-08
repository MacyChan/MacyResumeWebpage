package com.macydevelopment.springboot.repository;

import com.macydevelopment.springboot.model.AudioFeaturesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioFeaturesRepository extends JpaRepository<AudioFeaturesModel, Long> {
}