package com.aircompany.sales.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class NotificationInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationInitializer.class);
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("NotificationInitializer: Real notification system active - no test data needed");
        // Test notification initialization disabled - using real notifications only
    }
}