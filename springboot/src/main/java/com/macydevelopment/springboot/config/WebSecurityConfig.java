package com.macydevelopment.springboot.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http//.antMatcher("/**")
                .authorizeRequests( a ->
                                    a.antMatchers("/", "/index**","/test**", "/login**", "/get**", "/callback/", "/webjars/**", "/error**", "/oauth2/authorization/**")
                                    .permitAll()
                //.antMatchers("/MusicAndMood**")
                //.permitAll()
                                    .anyRequest()
                                    .authenticated()
                )
                .oauth2Login()
                       /* oauth2Login -> oauth2Login.userInfoEndpoint(
                                userInfoEndpointConfig -> userInfoEndpointConfig.oidcUserService(
                                        this.oidcUserService()
                                )
                        )
                )*/
                       .and()
                .csrf(c -> c
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .formLogin().disable()
                .logout().logoutSuccessUrl("/index.html").permitAll();
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**", "/fonts/**", "/scss/**","/vendor/**");
    }


}

