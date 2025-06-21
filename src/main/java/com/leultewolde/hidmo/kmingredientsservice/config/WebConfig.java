package com.leultewolde.hidmo.kmingredientsservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // apply to all endpoints
                .allowedOrigins("*")   // allow any origin
                .allowedMethods("*")   // allow all HTTP methods
                .allowedHeaders("*");  // allow all headers
    }
}
