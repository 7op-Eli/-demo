package com.property.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("物业管理系统 API")
                        .version("1.0.0")
                        .description("智慧物业管理系统接口文档 — 支持业主端、员工端、管理端")
                        .contact(new Contact().name("Property Team"))
                        .license(new License().name("Apache 2.0")));
    }
}
