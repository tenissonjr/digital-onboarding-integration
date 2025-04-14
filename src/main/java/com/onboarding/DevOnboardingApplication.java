package com.onboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class DevOnboardingApplication {

    public static void main(String[] args) {
        // Set development profile
        System.setProperty("spring.profiles.active", "dev");
        
        SpringApplication.run(DigitalOnboardingApplication.class, args);
    }
}