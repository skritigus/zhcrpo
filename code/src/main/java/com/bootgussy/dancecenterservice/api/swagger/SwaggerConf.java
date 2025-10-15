package com.bootgussy.dancecenterservice.api.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class SwaggerConf {
    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dance Center Swagger API")
                        .version("1.0")
                        .description("API for managing dance center"));
    }
}