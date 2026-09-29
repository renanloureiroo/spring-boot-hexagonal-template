package com.renanloureiroo.stepbystep.infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.renanloureiroo.stepbystep")
@ConfigurationPropertiesScan("com.renanloureiroo.stepbystep")
@EntityScan("com.renanloureiroo.stepbystep")
@EnableJpaRepositories("com.renanloureiroo.stepbystep")
public class StepByStepApplication {

  public static void main(String[] args) {
    SpringApplication.run(StepByStepApplication.class, args);
  }
}
