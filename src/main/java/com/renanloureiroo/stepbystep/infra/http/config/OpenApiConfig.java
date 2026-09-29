package com.renanloureiroo.stepbystep.infra.http.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class OpenApiConfig {

  @Bean
  public OpenAPI stepByStepOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Step by Step API")
                .version("v1")
                .description("API de exemplo. Erros seguem RFC 9457 e incluem um code estável."));
  }
}
