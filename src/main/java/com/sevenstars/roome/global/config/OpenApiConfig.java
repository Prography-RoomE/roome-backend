package com.sevenstars.roome.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "RoomE API Document",
                description = "API Document",
                version = "v0.1",
                termsOfService = "https://roome.site/",
                license = @License(
                        name = "Apache License Version 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0"
                ),
                contact = @Contact(
                        name = "JunHwan KIm",
                        email = "jjunhwan-kim@naver.com"
                )
        ),
        tags = {
                @Tag(name = "공통", description = "공통 API"),
                @Tag(name = "인증", description = "인증 API"),
                @Tag(name = "사용자", description = "사용자 API"),
                @Tag(name = "프로필", description = "프로필 API"),
                @Tag(name = "후기", description = "후기 API"),
                @Tag(name = "크롤링", description = "크롤링 API"),
        }
        ,
        servers = {
                @Server(url = "/"),
        }
)
@SecurityScheme(
        name = "bearerAuth",
        scheme = "bearer",
        bearerFormat = "JWT",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER
)
@ConditionalOnProperty(name = "spring-doc.swagger-ui.enabled", havingValue = "true")
@Configuration
public class OpenApiConfig {
}
