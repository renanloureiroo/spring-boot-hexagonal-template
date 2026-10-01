package com.renanloureiroo.hexagonal.infra.http.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class OpenApiConfig {

  @Bean
  public OpenAPI hexagonalOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Hexagonal Template API")
                .version("v1")
                .description("API de exemplo. Erros seguem RFC 9457 e incluem um code estável."));
  }
}
