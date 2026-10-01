# Spring Boot Hexagonal Template

Template de backend Java 21 com Spring Boot, arquitetura modular, PostgreSQL,
Flyway, OpenTelemetry e Testcontainers.

<!-- template:start -->
Existe uma versão Node.js equivalente, com a mesma arquitetura, os mesmos contratos de
erro e os mesmos tipos de teste:
[nestjs-hexagonal-template](https://github.com/renanloureiroo/nestjs-hexagonal-template).

<!-- template:end -->
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
docker build -t spring-boot-hexagonal-template .
```

<!-- template:start -->
## Usando como template

Clique em **Use this template** e dê ao repositório o nome do projeto, por exemplo
`orders-service`. No primeiro push, o workflow `template-init` renomeia tudo a
partir desse nome e commita o resultado:

| Item | Exemplo para `orders-service` |
|---|---|
| Pacote | `com.<owner>.ordersservice` |
| Classe principal | `OrdersServiceApplication` |
| `artifactId` e imagem | `orders-service` |
| `spring.application.name` | `orders-service` |
| Banco e usuário locais | `orders_service` |
| Título da API | `Orders Service API` |

Aguarde o workflow terminar (aba **Actions**, cerca de 30 s) antes de clonar.

Para inicializar localmente, depois de clonar:

```bash
scripts/init-template.sh orders-service com.acme
```

Depois da inicialização:

1. ajuste `description` no `pom.xml`;
2. remova ou renomeie `modules/example` e sua migration;
3. substitua este README pela apresentação do produto;
4. revise as decisões abertas em `docs/ARCHITECTURE.md`.

<!-- template:end -->
## Documentação

- [Arquitetura](docs/ARCHITECTURE.md)
- [Testes](docs/TESTS.md)
