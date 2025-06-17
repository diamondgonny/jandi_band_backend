package com.jandi.band_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableElasticsearchRepositories(basePackages = "com.jandi.band_backend.search.repository")
@SpringBootApplication
public class JandiBandBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JandiBandBackendApplication.class, args);
    }

}
