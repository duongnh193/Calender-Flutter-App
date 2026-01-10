package com.duong.lichvanien.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lich Van Nien API")
                        .description("""
                                API documentation for Lich Van Nien application.
                                
                                ## Features
                                - **Calendar**: Solar/Lunar calendar conversion
                                - **Zodiac**: Vietnamese zodiac information
                                - **Horoscope**: Daily, monthly, yearly horoscopes
                                - **Tu Vi Chart**: Purple Star Astrology chart generation and interpretation
                                - **User Management**: Registration, authentication, profile management
                                - **Payment**: Payment tracking and content access control
                                
                                ## Authentication
                                Most endpoints are public. Protected endpoints require JWT Bearer token.
                                
                                To authenticate:
                                1. Register: POST /api/v1/user/register
                                2. Login: POST /api/v1/user/login
                                3. Use the returned `accessToken` in Authorization header: `Bearer <token>`
                                
                                ## Fingerprint
                                For anonymous tracking and payment verification, include fingerprint data in header:
                                - Header: `X-Fingerprint-Data` (JSON)
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Lich Van Nien Team")
                                .email("support@lichvannien.vn")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT access token from login/register endpoint")));
    }
}
