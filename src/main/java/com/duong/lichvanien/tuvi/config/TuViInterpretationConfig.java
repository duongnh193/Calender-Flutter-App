package com.duong.lichvanien.tuvi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Tu Vi interpretation service.
 * All interpretations are stored in database and must be pre-seeded.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "tuvi.interpretation")
public class TuViInterpretationConfig {

    /**
     * Whether database storage is enabled.
     * Default: true (always enabled in DB-only mode)
     */
    private boolean databaseEnabled = true;
}
