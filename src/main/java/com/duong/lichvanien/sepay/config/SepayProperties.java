package com.duong.lichvanien.sepay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * SePay configuration properties.
 */
@Data
@Component
@ConfigurationProperties(prefix = "sepay")
public class SepayProperties {

    /**
     * SePay API key.
     */
    private String apiKey;

    /**
     * Bank account number.
     */
    private String bankAccount;

    /**
     * Bank name.
     */
    private String bankName;

    /**
     * Account holder name.
     */
    private String accountName;

    /**
     * Webhook secret for verifying webhook requests.
     */
    private String webhookSecret;

    /**
     * Content prefix for transfer content.
     */
    private String contentPrefix = "LVNXU";
}

