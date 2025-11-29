package com.nexus.investment_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private static final Logger log = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${user.service.url:http://localhost:3000}")
    private String userServiceUrl;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        String baseUrl = userServiceUrl + "/api/v1/users";
        log.info("Configuring WebClient baseUrl={}", baseUrl);
        return builder
                .baseUrl(baseUrl)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                        .build())
                .build();
    }
}

