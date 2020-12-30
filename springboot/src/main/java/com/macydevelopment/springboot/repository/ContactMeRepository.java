package com.macydevelopment.springboot.repository;

import com.macydevelopment.springboot.model.ContactMeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactMeRepository extends JpaRepository<ContactMeModel, Long> {
}