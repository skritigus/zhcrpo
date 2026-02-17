package com.bootgussy.dancecenterservice.api.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.context.annotation.Bean;

public class SwaggerConf {
    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .components(new Components()
                        .addParameters("X-Role", new Parameter()
                                .in("header")
                                .name("X-Role")
                                .description("Введите роль: ROLE_ADMIN, ROLE_STUDENT или ROLE_TRAINER")
                                .schema(new StringSchema()._default("ROLE_ADMIN"))
                                .required(false)))
                .info(new Info()
                        .title("Dance Center Swagger API")
                        .version("1.0")
                        .description("API for managing dance center"));
    }
}