package com.macydevelopment.springboot.controller;

import com.macydevelopment.springboot.model.ContactMeModel;
import com.macydevelopment.springboot.repository.ContactMeRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
public class IndexController {

    @Autowired
    private ContactMeRepository contactMeRepository;

    /*
    @GetMapping("/api/contactMe")
    public Page<ContactMeModel> getContactMe(Pageable pageable) {
        return contactMeRepository.findAll(pageable);
    }
    */

    @PostMapping("/db/contactMe")
    public ContactMeModel createContactMe(@Valid @RequestBody ContactMeModel contactMeModel) {
        return contactMeRepository.save(contactMeModel);
    }

    //https://www.callicoder.com/spring-boot-jpa-hibernate-postgresql-restful-crud-api-example/


}
