package com.macydevelopment.springboot.service;

import com.macydevelopment.springboot.model.AccessTokenRecord;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StartUpService
{
    public List<AccessTokenRecord> listAccessTokenRecords = new ArrayList<AccessTokenRecord>();

}
