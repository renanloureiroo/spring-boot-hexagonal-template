package com.renanloureiroo.hexagonal.infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.renanloureiroo.hexagonal")
@ConfigurationPropertiesScan("com.renanloureiroo.hexagonal")
@EntityScan("com.renanloureiroo.hexagonal")
@EnableJpaRepositories("com.renanloureiroo.hexagonal")
public class HexagonalApplication {

  public static void main(String[] args) {
    SpringApplication.run(HexagonalApplication.class, args);
  }
}
