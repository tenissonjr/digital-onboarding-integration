package com.onboarding.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.onboarding.domain.repository")
@EnableTransactionManagement
public class DataSourceConfig {
    // H2 configuration is automatically managed through application.properties
}