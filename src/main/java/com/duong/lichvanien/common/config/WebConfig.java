package com.duong.lichvanien.common.config;

import com.duong.lichvanien.user.interceptor.AccessLogInterceptor;
import com.duong.lichvanien.user.interceptor.FingerprintInterceptor;
import com.duong.lichvanien.user.interceptor.PaymentCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for interceptors.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final FingerprintInterceptor fingerprintInterceptor;
    private final AccessLogInterceptor accessLogInterceptor;
    private final PaymentCheckInterceptor paymentCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Fingerprint interceptor - applies to all API endpoints
        registry.addInterceptor(fingerprintInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/actuator/**"
                )
                .order(1);

        // Access log interceptor - applies to all API endpoints
        registry.addInterceptor(accessLogInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/actuator/**"
                )
                .order(2);

        // Payment check interceptor - applies to paid content endpoints
        registry.addInterceptor(paymentCheckInterceptor)
                .addPathPatterns(
                        "/api/v1/tuvi/chart/interpretation/**",
                        "/api/v1/horoscope/lifetime/**"
                )
                .order(3);
    }
}

