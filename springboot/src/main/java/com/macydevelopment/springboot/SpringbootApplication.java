package com.macydevelopment.springboot;

import com.macydevelopment.springboot.config.SpotifyPropertiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(SpotifyPropertiesConfig.class)

public class SpringbootApplication {


    public static void main(String[] args) {


        SpringApplication.run(SpringbootApplication.class, args);

    }

    @Bean
    SpotifyPropertiesConfig spotifyPropertiesConfig() {
        return new SpotifyPropertiesConfig();
    }


};
