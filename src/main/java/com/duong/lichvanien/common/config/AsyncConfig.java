package com.duong.lichvanien.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration to enable async processing for access logging.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    // Default Spring async executor will be used
    // For production, consider configuring a custom ThreadPoolTaskExecutor
}

