package com.renanloureiroo.stepbystep.testsupport.annotations;

import com.renanloureiroo.stepbystep.infra.StepByStepApplication;
import com.renanloureiroo.stepbystep.infra.TestcontainersConfiguration;
import com.renanloureiroo.stepbystep.testsupport.database.DatabaseCleaner;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Tag("e2e")
@Import({TestcontainersConfiguration.class, DatabaseCleaner.class})
@AutoConfigureRestTestClient
@SpringBootTest(
    classes = StepByStepApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"springdoc.api-docs.enabled=true", "springdoc.swagger-ui.enabled=true"})
public @interface E2E {}
