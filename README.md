# Spring Boot Step by Step

Template de backend Java 21 com Spring Boot, arquitetura modular, PostgreSQL,
Flyway, OpenTelemetry e Testcontainers.

O módulo `example` implementa uma API pequena de notas para mostrar o caminho
completo: domínio → caso de uso → porta → JPA → HTTP. Ele deve ser removido ou
renomeado quando o primeiro contexto real for criado.

## Requisitos

- Java 21;
- Docker;
- Docker Compose.

## Rodando localmente

Crie a configuração local a partir do exemplo:

```bash
cp src/main/resources/application-local.yml.example \
  src/main/resources/application-local.yml
./mvnw spring-boot:run
```

O profile `local` é ativado pelo plugin Maven. A aplicação fica disponível em:

- API: `http://localhost:8080/api`;
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`;
- Health: `http://localhost:8080/api/actuator/health`;
- Grafana: `http://localhost:3000`.

Exemplo:

```bash
curl -i -X POST http://localhost:8080/api/notes \
  -H 'Content-Type: application/json' \
  -d '{"title":"Minha primeira nota"}'
```

## Testes

```bash
./mvnw test
./mvnw verify
./mvnw test -DexcludedGroups=e2e
./mvnw test -Dgroups=e2e
```

Os testes E2E exigem Docker. Veja [docs/TESTS.md](docs/TESTS.md).

## Imagem da aplicação

```bash
docker build -t step-by-step .
```

## Usando como template

Depois de criar um repositório a partir deste template:

1. altere `groupId`, `artifactId`, `name` e `description` no `pom.xml`;
2. renomeie o pacote `com.renanloureiroo.stepbystep`;
3. ajuste `spring.application.name` e as credenciais locais;
4. remova ou renomeie `modules/example` e sua migration;
5. atualize os metadados em `OpenApiConfig`;
6. substitua este README pela apresentação do produto;
7. revise as decisões abertas em `docs/ARCHITECTURE.md`.

No GitHub, habilite **Settings → General → Template repository**.

## Documentação

- [Arquitetura](docs/ARCHITECTURE.md)
- [Testes](docs/TESTS.md)
