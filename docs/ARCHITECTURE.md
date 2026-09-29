# Arquitetura do backend

Este documento define como o backend do **Step by Step** deve ser organizado e
como novas funcionalidades devem ser construídas. O projeto ainda está no início;
portanto, este texto é prescritivo. Quando código e documento divergirem, a
divergência deve ser resolvida no mesmo pull request.

O padrão adota um núcleo independente de framework, módulos por contexto de
negócio, portas e adaptadores explícitos e testes como contrato.

Veja também [TESTS.md](TESTS.md).

---

## 1. Stack de referência

- Java 21;
- Spring Boot 4.1, com Web MVC, Validation, Data JPA e Actuator;
- PostgreSQL e Flyway;
- OpenAPI com springdoc;
- OpenTelemetry;
- JUnit Jupiter, AssertJ e Testcontainers;
- Maven Wrapper.

Versões exatas ficam no `pom.xml`. Dependência nova precisa resolver um problema
concreto; não se adiciona infraestrutura por antecipação.

## 2. Regra principal

A dependência aponta em uma única direção:

```text
infra  ─────►  application  ─────►  domain
  │                                      ▲
  └────────────────►  core  ◄────────────┘
```

`infra` conhece Spring, HTTP, JPA e serviços externos. `application`, `domain` e
`core` são Java puro e não conhecem detalhes de entrega ou persistência.

São proibidos em `core`, `modules/*/domain` e `modules/*/application`:

- `org.springframework.*`;
- `jakarta.persistence.*` e `jakarta.validation.*`;
- `io.swagger.*`;
- tipos de protocolo, como `HttpStatus` e `ResponseEntity`.

Lombok só é aceito quando não introduz semântica de framework, por exemplo
`@Getter` e `@Slf4j`.

## 3. Estrutura do código

O namespace inicial será `com.renanloureiroo.stepbystep`:

```text
src/main/java/com/renanloureiroo/stepbystep
├── core
│   ├── entity
│   ├── error
│   ├── identity
│   ├── pagination
│   ├── transaction
│   └── usecase
├── infra
│   ├── StepByStepApplication.java
│   ├── http
│   │   ├── config
│   │   ├── dtos
│   │   └── error
│   └── transaction
└── modules
    └── <contexto>
        ├── domain
        │   ├── entities
        │   └── valueobjects
        ├── application
        │   ├── errors
        │   ├── gateways
        │   ├── repositories
        │   ├── services
        │   └── usecases
        └── infra
            ├── config
            ├── database/jpa
            │   ├── entities
            │   ├── mappers
            │   └── repositories
            ├── gateways
            └── http
                ├── controllers
                ├── dtos
                └── presenters
```

Um módulo representa um contexto de negócio, não uma entidade ou tabela. Algo só
sobe para `core` quando pelo menos dois módulos realmente precisarem dele.

Módulos não importam o domínio uns dos outros. Quando um contexto precisa de uma
capacidade de outro, declara uma porta em `application/gateways`; o adaptador em
`infra/gateways` faz a travessia.

## 4. Modelo de domínio

### Entidades e identificadores

Uma entidade tem identidade e estende `Entity<ID extends Id>`. Cada identificador
é opaco, tipado e oferece duas factories:

```java
public final class ResourceId extends Id {
  private ResourceId(String value) {
    super(value);
  }

  public static ResourceId generate() {
    return new ResourceId(newValue());
  }

  public static ResourceId of(String value) {
    return new ResourceId(value);
  }
}
```

Entidades possuem duas entradas explícitas:

- `create(...)`, para um objeto que nasce agora;
- `restore(...)`, para reconstrução a partir da persistência.

Mapper JPA sempre chama `restore`, nunca `create`.

Quando uma entidade delimita uma fronteira de consistência e produz eventos de
domínio, ela estende `AggregateRoot<ID>`. A base registra `DomainEvent` e expõe
`pullDomainEvents()`, que devolve e limpa os eventos pendentes. O módulo de exemplo
demonstra o ciclo completo com `NoteCreated`, `DomainEventPublisher` e um
`NoteCreatedListener` executado após o commit.

### Value objects e invariantes

Value object é definido pelo conteúdo e deve ser um `record`. Toda invariante é
validada no compact constructor ou no construtor privado da entidade. Um tipo de
domínio construído é sempre válido; casos de uso não repetem sua validação.

Ausência em leitura é representada por `Optional`, nunca por `null` devolvido ao
chamador. Tempo é `Instant`, armazenado e processado em UTC.

Entidades que não produzem eventos continuam estendendo `Entity`; `AggregateRoot`
não é usado apenas como marcador.

## 5. Erros

Todo erro da aplicação estende `ApplicationException` e carrega:

- `ErrorType`, independente de HTTP;
- `code` estável no formato `<contexto>.<motivo>`.

O `code` é contrato público. A mensagem é texto humano e pode evoluir. Códigos
ficam em constantes `private static final` no tipo que os lança.

Famílias iniciais:

| `ErrorType` | Semântica | HTTP |
|---|---|---:|
| `NOT_FOUND` | recurso inexistente | 404 |
| `CONFLICT` | estado atual impede a operação | 409 |
| `VALIDATION` | formato ou entrada inválida | 400 |
| `UNAUTHORIZED` | identidade ausente ou inválida | 401 |
| `FORBIDDEN` | identidade sem permissão | 403 |
| `BUSINESS_RULE` | regra de negócio violada | 422 |

A conversão para status mora exclusivamente em `ErrorTypeHttpStatus`. Erro
recorrente ganha uma classe nomeada em `application/errors` e carrega o dado útil
à borda, não apenas uma mensagem.

## 6. Casos de uso

Cada caso de uso:

- é um POJO sem `@Service`, `@Component` ou outra anotação de framework;
- implementa uma interface de `core.usecase`;
- possui um único método `execute`;
- declara `Input` e `Output` como `record` aninhados;
- recebe tipos da aplicação, nunca DTO HTTP;
- devolve um output, nunca uma entidade de domínio.

Um output compartilhado em `application/outputs` só nasce quando casos de uso
realmente compartilham o mesmo contrato semântico. Formatos apenas coincidentes
permanecem como outputs aninhados independentes.

As quatro assinaturas compartilhadas são:

```java
UseCase<I, O>                    // O execute(I input)
UseCaseWithoutInput<O>           // O execute()
UseCaseWithoutOutput<I>          // void execute(I input)
UseCaseWithoutInputAndOutput     // void execute()
```

O cabeamento fica em `modules/<contexto>/infra/config/UseCasesConfiguration`, com
um `@Bean` explícito por caso de uso.

## 7. Persistência e transações

A persistência de um agregado é formada por quatro peças:

1. porta em `application/repositories`, falando em tipos de domínio;
2. entidade JPA separada, em `infra/database/jpa/entities`;
3. mapper explícito domínio ↔ JPA;
4. adaptador que implementa a porta sobre um repositório Spring Data.

Entidade de domínio e entidade JPA nunca são a mesma classe. `EntityManager`
direto não é usado enquanto Spring Data resolver o caso de forma clara.

Transação é decisão do caso de uso. Ela entra quando duas escritas precisam ser
atômicas ou quando leitura e escrita precisam observar o mesmo instante do banco.
Uma escrita isolada não justifica transação explícita.

O domínio declara a intenção por uma abstração própria em `core.transaction`; a
infraestrutura implementa essa intenção com Spring. `@Transactional` do Spring em
repositório é proibido.

### Migrations

- ficam em `src/main/resources/db/migration`;
- seguem `V<yyyyMMddHHmmss>__descricao.sql`, com timestamp UTC;
- nunca são editadas depois de aplicadas;
- acompanham índice para todo campo usado em filtro, ordenação ou junção;
- convivem com `spring.jpa.hibernate.ddl-auto=validate`.

Unicidade de negócio deve existir também como constraint no banco.

## 8. Borda HTTP

Controllers apenas adaptam HTTP para a aplicação:

1. recebem e validam o DTO;
2. convertem o DTO para o `Input` do caso de uso;
3. executam o caso de uso;
4. passam o `Output` a um presenter;
5. montam status e headers.

Regras da borda:

- o prefixo global `/api` vem do `context-path`; controllers não o repetem;
- criação retorna `201` e header `Location`;
- DTO de entrada é `record` com Bean Validation e `toInput()`;
- mensagens das constraints espelham as invariantes correspondentes;
- presenter é `final`, tem construtor privado e método estático `present`;
- OpenAPI mora em uma interface `*Swagger` implementada pelo controller;
- todo status possível é documentado;
- `try/catch` para traduzir erro no controller é proibido.

Toda falha HTTP é produzida pelo `ApiExceptionHandler` em RFC 9457
(`application/problem+json`), incluindo `code` e, quando disponível, `traceId`.
Erros inesperados viram `internal.unexpected` sem expor detalhes internos.

## 9. Fluxo ponta a ponta

```text
requisição
   │
   ▼
DTO + Bean Validation
   │
   ▼
controller ─► input do caso de uso
   │
   ▼
caso de uso ─► domínio ─► porta de repositório
   │                              │
   │                              ▼
   │                    adaptador JPA ─► PostgreSQL
   ▼
output ─► presenter ─► resposta HTTP
```

Exceções sobem intactas até o `ApiExceptionHandler`. Nenhuma camada intermediária
as converte para conceitos HTTP.

## 10. Performance e observabilidade

- listagens públicas são paginadas;
- `Pageable` e `Page` do Spring não saem de `infra`;
- é proibida chamada a repositório ou I/O bloqueante dentro de laço;
- consultas de coleção não podem produzir N+1;
- cache só entra para resolver problema medido;
- logs usam `@Slf4j`;
- sucesso pode ser logado no caso de uso; erro é logado uma vez, na borda;
- estado de requisição não depende de `ThreadLocal` próprio;
- código de request não usa `synchronized` em torno de I/O.

Ainda não há SLO numérico. Ele será definido quando existir baseline real de
tráfego e latência.

## 11. Ordem de implementação

Para cada funcionalidade:

1. value objects e entidade, precedidos por testes de invariantes;
2. porta de repositório e fake em `testsupport`;
3. caso de uso, precedido por testes sobre o fake;
4. migration, entidade JPA, mapper e adaptador;
5. DTO de entrada, precedido por testes de constraints;
6. presenter, contrato OpenAPI e controller;
7. teste E2E;
8. bean no `UseCasesConfiguration`.

Correção de bug começa por um teste que reproduz o defeito.

## 12. Convenções gerais

- identificadores, nomes de campos JSON e códigos de erro em inglês;
- mensagens de erro, `@Schema` e `@DisplayName` em português;
- google-java-format aplicado pelo editor, sem plugin de formatação no Maven;
- comentários explicam decisões e alternativas descartadas, não a assinatura;
- abstração nova somente quando o segundo caso concreto a exigir.

## 13. Checklist de arquitetura

- [ ] Nenhum framework em `core`, `domain` ou `application`.
- [ ] Invariantes pertencem ao domínio.
- [ ] Caso de uso é POJO e recebe `Input`, não DTO.
- [ ] Portas ficam em `application`; adaptadores, em `infra`.
- [ ] Entidades de domínio e JPA são separadas.
- [ ] Erros possuem `type` e `code` estável.
- [ ] Controller não contém regra nem tradução local de erro.
- [ ] OpenAPI anuncia todos os status possíveis.
- [ ] Toda alteração de schema possui migration nova.
- [ ] Listagens são paginadas e consultas não produzem N+1.
- [ ] `./mvnw verify` está verde.

## 14. Decisões ainda abertas

Não devem ser inventadas antes do primeiro caso concreto:

- contextos de negócio e agregados;
- autenticação e autorização;
- integrações externas;
- estratégia durável para eventos externos, como outbox;
- cache;
- SLOs numéricos;
- estratégia de deployment.

Quando uma decisão dessas for tomada, ela deve atualizar este documento e, se
tiver alternativas relevantes ou custo duradouro, ganhar um ADR.
