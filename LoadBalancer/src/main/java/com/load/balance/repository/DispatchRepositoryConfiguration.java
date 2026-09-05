package com.load.balance.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DispatchRepositoryConfiguration {
    @Bean
    public DispatchRepository dispatchRepository() {
        return new DispatchRepository();
    }
}