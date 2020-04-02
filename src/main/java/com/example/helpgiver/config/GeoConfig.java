package com.example.helpgiver.config;

import com.fasterxml.jackson.databind.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.geo.GeoJsonModule;

@Configuration
public class GeoConfig {
    @Bean
    public Module registerGeoJsonModule(){
        return new GeoJsonModule();
    }
}
