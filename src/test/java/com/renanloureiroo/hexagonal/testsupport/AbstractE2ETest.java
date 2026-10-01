package com.renanloureiroo.hexagonal.testsupport;

import com.renanloureiroo.hexagonal.infra.HexagonalApplication;
import com.renanloureiroo.hexagonal.testsupport.database.DatabaseCleaner;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(
    classes = HexagonalApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"springdoc.api-docs.enabled=true", "springdoc.swagger-ui.enabled=true"})
@AutoConfigureRestTestClient
@Tag("e2e")
@Import(DatabaseCleaner.class)
public abstract class AbstractE2ETest {

    @Container
    @ServiceConnection
    protected static final PostgreSQLContainer POSTGRES =
        new PostgreSQLContainer(DockerImageName.parse("postgres:latest"));
}
