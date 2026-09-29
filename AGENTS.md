# AGENTS.md

Backend Java 21 com Spring Boot, PostgreSQL, Flyway, OpenTelemetry, Maven e
Testcontainers.

A dependência aponta em uma única direção: `infra` conhece `application` e
`domain`; o núcleo não conhece Spring, HTTP ou JPA.

Antes de alterar código, leia:

- [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md)
- [`docs/TESTS.md`](docs/TESTS.md)

Regras essenciais:

- casos de uso são POJOs cabeados por `@Bean`;
- invariantes vivem no domínio;
- persistência usa porta, adaptador e entidade JPA separada;
- mocks sobre portas do projeto são proibidos;
- erros HTTP seguem RFC 9457 e carregam `code` estável;
- migrations aplicadas nunca são editadas;
- o portão de entrega é `./mvnw verify`.
