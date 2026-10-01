# Testes do backend

Este documento define como provar o comportamento de um backend criado a partir deste template.
Ele complementa [ARCHITECTURE.md](ARCHITECTURE.md): a arquitetura descreve as
fronteiras; este documento define o teste adequado para cada uma.

O objetivo não é maximizar cobertura por métrica. É manter contratos observáveis,
invariantes de domínio e integrações críticas protegidos com o teste mais barato
capaz de demonstrar cada comportamento.

---

## 1. Princípios

### Fakes para portas do projeto

Mock sobre uma porta do projeto é proibido. Repositórios, gateways e o transactor
possuem implementações de teste reais em `testsupport`.

Um caso de uso deve ser testado pelo estado resultante no fake, não por
`verify(repository).save(...)`. Isso prova o comportamento e mantém o teste estável
quando a implementação interna muda.

Mock é aceitável apenas para a fronteira de uma biblioteca de terceiro quando um
fake real custaria mais do que a integração que está sendo verificada, por exemplo
um `Tracer`.

### Contrato do erro

Testes de domínio e aplicação afirmam `ErrorType` e `code`, nunca a mensagem:

```java
assertThatThrownBy(() -> Value.of(invalid))
    .isInstanceOf(DomainException.class)
    .satisfies(error -> {
      var domainError = (DomainException) error;
      assertThat(domainError.type()).isEqualTo(ErrorType.VALIDATION);
      assertThat(domainError.code()).isEqualTo("context.value_invalid");
    });
```

A mensagem exata só é contrato em dois lugares:

- teste do DTO, para provar que a constraint espelha o domínio;
- E2E, para provar o que o cliente efetivamente recebe.

### Teste mais barato primeiro

O contexto Spring só sobe quando a integração entre as peças é o objeto do teste.
Combinações de entrada, limites e invariantes pertencem a testes unitários rápidos.

## 2. Tipos de teste

| Tipo | Contexto Spring | Ferramenta | O que prova |
|---|---|---|---|
| Value object / entidade | não | JUnit + AssertJ | invariantes e transições |
| Caso de uso | não | JUnit + fake in-memory | orquestração e estado final |
| DTO / validação | não | Jakarta Validator | constraints e `toInput()` |
| Componente HTTP isolado | não | MockMvc `standaloneSetup` | tradução na borda |
| Adaptador | não | fake do colaborador | protocolo de integração |
| E2E | sim | `@E2E` + Testcontainers + `RestTestClient` | contrato HTTP ponta a ponta |

Testes E2E iniciam a aplicação com `SpringBootTest.WebEnvironment.RANDOM_PORT`.
Isso usa um servidor real em uma porta livre e permite exercitar serialização,
validação, transação e persistência como ocorrerão em produção.

## 3. Estrutura

A árvore de testes espelha a de produção e concentra utilidades compartilhadas em
`testsupport`:

```text
src/test/java/com/renanloureiroo/hexagonal
├── core
├── infra
├── modules
│   └── <contexto>
│       ├── domain
│       ├── application
│       └── infra
└── testsupport
    ├── annotations/E2E.java
    ├── database/DatabaseCleaner.java
    ├── factories
    ├── gateways
    ├── repositories
    └── transaction/DirectTransactor.java
```

## 4. Convenções

- classe: `<Alvo>Test`; ponta a ponta: `<Alvo>E2ETest`;
- classes e métodos package-private;
- métodos em português, `snake_case`, descrevendo comportamento;
- `@DisplayName` em português quando acrescentar clareza;
- sujeito sob teste chamado `sut` quando montado no `@BeforeEach`;
- asserções exclusivamente com AssertJ;
- famílias de entrada em `@ParameterizedTest`;
- montagem, ação e asserção separadas por linha em branco;
- sem comentários `given/when/then` que apenas repitam a estrutura;
- nenhum teste desabilitado sem justificativa escrita.

Use `@NullAndEmptySource` com uma entrada branca para cobrir o trio padrão de
ausência: `null`, vazio e apenas espaços.

## 5. Value objects

Todo caminho de construção é exercitado sem Spring:

- formatos válidos;
- formatos inválidos;
- ausência;
- os dois lados de cada limite;
- `type` e `code` de cada erro;
- igualdade estrutural;
- `toString()` quando ele representa o valor externo;
- derivação e normalização, quando existirem.

```java
@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {"   "})
void rejeita_valor_ausente(String invalid) {
  assertThatThrownBy(() -> Value.of(invalid))
      .isInstanceOf(DomainException.class)
      .extracting(error -> ((DomainException) error).code())
      .isEqualTo("context.value_invalid");
}

@Test
void respeita_o_limite_maximo() {
  assertThat(Value.of("a".repeat(200)).value()).hasSize(200);
  assertThatThrownBy(() -> Value.of("a".repeat(201)))
      .isInstanceOf(DomainException.class);
}
```

O mesmo teste deve mostrar que 200 passa e 201 falha; testar apenas o lado inválido
não demonstra qual é o limite.

## 6. Entidades

Teste comportamento e transição, não getters isolados:

- estado ao nascer por `create`;
- identidade própria;
- cada método de negócio e seu efeito;
- atualização de `updatedAt`;
- rejeição de transições inválidas;
- preservação de identidade e timestamps por `restore`;
- igualdade por tipo concreto e identificador.

Quando a classe usa o relógio do sistema, evite igualdade com um instante exato.
Capture o instante anterior e afirme `isAfterOrEqualTo`, ou introduza uma porta de
relógio apenas quando o domínio exigir controle determinístico do tempo.

## 7. Casos de uso

O setup usa fakes reais:

```java
private ResourceRepository resources;
private CreateResourceUseCase sut;

@BeforeEach
void setUp() {
  resources = new InMemoryResourceRepository();
  sut = new CreateResourceUseCase(resources);
}
```

Cada caso de uso cobre:

- caminho feliz completo;
- variações de entrada que alteram o resultado;
- cada erro que pode lançar;
- dado carregado por um erro nomeado;
- estado final no fake;
- ausência de mudança em todos os caminhos de falha;
- passagem pelo `DirectTransactor`, quando houver transação explícita.

```java
@Test
void nao_altera_o_estado_quando_a_regra_falha() {
  var existing = ResourceFactory.aResource().buildSavedIn(resources);

  assertThatThrownBy(() -> sut.execute(duplicatedInput()))
      .isInstanceOf(ResourceAlreadyExists.class);

  assertThat(resources.findAll()).containsExactly(existing);
}
```

## 8. Factories e fakes

Cada agregado possui uma factory fluente com defaults válidos e, quando fizer
sentido:

```java
ResourceFactory.aResource().build();
ResourceFactory.aResource().withName("Outro").build();
ResourceFactory.aResource().buildSavedIn(resources);
ResourceFactory.aResource().asCreateInput();
```

Construção manual repetida é proibida. Quando um campo obrigatório for adicionado,
o default deve mudar em um único lugar.

Fakes in-memory preservam comportamento relevante da porta, como unicidade,
paginação e ordem. Eles não simulam SQL; o que depende do banco é provado pelo E2E.

## 9. DTOs e Bean Validation

DTO é testado com `Validation.buildDefaultValidatorFactory()`, sem contexto Spring.
O teste verifica:

- payload mínimo e completo válidos;
- cada constraint e sua mensagem exata;
- todos os campos inválidos reportados juntos;
- conversão correta por `toInput()`;
- alinhamento entre mensagem da constraint e invariante do domínio.

```java
private static ValidatorFactory factory;
private static Validator validator;

@BeforeAll
static void startValidator() {
  factory = Validation.buildDefaultValidatorFactory();
  validator = factory.getValidator();
}

@AfterAll
static void closeValidator() {
  factory.close();
}
```

## 10. Componentes de borda e adaptadores

Um `@RestControllerAdvice` é testado com MockMvc `standaloneSetup` e um controller
de teste que lança os erros necessários. Deve ser provado que:

- cada `ErrorType` recebe o status esperado;
- a resposta segue RFC 9457;
- `code` e `traceId` aparecem quando aplicável;
- erro inesperado vira `internal.unexpected`;
- mensagens internas e segredos não vazam na resposta.

Adaptadores que conversam com APIs de framework usam colaboradores de teste que
registram eventos. Um adaptador transacional, por exemplo, deve provar as sequências
`begin → commit` e `begin → rollback`, além de preservar a mesma exceção original.

## 11. Infraestrutura E2E

### `@E2E`

A anotação composta concentra a configuração comum:

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag("e2e")
@Import({TestcontainersConfiguration.class, DatabaseCleaner.class})
@AutoConfigureRestTestClient
@SpringBootTest(
    classes = HexagonalApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public @interface E2E {}
```

Não se adicionam `properties`, `@Import` ou mocks diferentes a testes individuais
sem necessidade comprovada. Cada variação na configuração pode criar outro contexto
Spring e outra pilha de containers.

### Testcontainers

O E2E usa PostgreSQL real em Testcontainers. Nenhum serviço compartilhado de
desenvolvimento ou ambiente remoto participa da suíte. O mesmo contexto Spring e
os mesmos containers devem ser reaproveitados entre classes durante uma execução.

Se a aplicação vier a depender de outro serviço de infraestrutura, seu container
só entra na suíte quando a dependência também entrar no comportamento de produção.

### `DatabaseCleaner`

Cada teste começa com banco limpo:

```java
@Autowired DatabaseCleaner database;

@BeforeEach
void setUp() {
  database.clean();
}
```

O cleaner descobre tabelas pelo catálogo do PostgreSQL e executa `TRUNCATE ...
RESTART IDENTITY CASCADE`, sem codificar a ordem de chaves estrangeiras.

**Nunca use `@Transactional` em E2E.** Com servidor em porta aleatória, a requisição
é atendida em outra thread. A transação do teste não representa a transação da
aplicação e seu rollback não limpa o commit feito pelo servidor.

## 12. Contrato mínimo de endpoint

Um endpoint só está pronto quando o E2E cobre:

- [ ] caminho feliz: status, corpo e headers;
- [ ] `Location` em criação;
- [ ] estado persistido lido novamente do banco;
- [ ] validação: 400, `request.invalid` e erros por campo;
- [ ] cada erro de negócio anunciado no OpenAPI;
- [ ] `application/problem+json` nos erros;
- [ ] JSON malformado sem vazamento do erro de parsing;
- [ ] ausência de mudança no banco em cada caminho de falha.

Não use E2E para varrer dezenas de combinações de entrada. O E2E prova que as peças
se integram; value objects e DTOs provam as combinações.

Exemplo de forma do caminho feliz:

```java
@E2E
@DisplayName("POST /resources")
class CreateResourceE2ETest {

  @Autowired RestTestClient client;
  @Autowired ResourceJpaRepository resources;
  @Autowired DatabaseCleaner database;

  @BeforeEach
  void setUp() {
    database.clean();
  }

  @Test
  void cria_retorna_location_e_persiste() {
    var result = client.post()
        .uri("/resources")
        .contentType(MediaType.APPLICATION_JSON)
        .body(validRequest())
        .exchange()
        .expectStatus().isCreated()
        .expectBody(CreateResourceResponseDTO.class)
        .returnResult();

    var body = result.getResponseBody();

    assertThat(body).isNotNull();
    assertThat(result.getResponseHeaders().getLocation())
        .asString()
        .endsWith("/resources/" + body.id());
    assertThat(resources.findById(body.id())).isPresent();
  }
}
```

Os nomes `Resource` e `/resources` são apenas exemplos; devem ser substituídos pela
linguagem do primeiro contexto de negócio.

## 13. Ordem de escrita

| Passo | Produção | Teste que vem antes |
|---:|---|---|
| 1 | value object e entidade | invariantes e transições |
| 2 | porta e fake | contrato implementável pelo fake |
| 3 | caso de uso | comportamento sobre o fake |
| 4 | migration e adaptador JPA | integração coberta pelo E2E |
| 5 | DTO | constraints e `toInput()` |
| 6 | presenter, OpenAPI e controller | componentes isolados, se necessário |
| 7 | endpoint completo | E2E do contrato HTTP |

Uma correção de bug começa com um teste vermelho que reproduz o defeito.

## 14. Execução

Quando o Maven Wrapper for criado, os comandos padrão serão:

```bash
./mvnw test
./mvnw verify
./mvnw test -Dtest=ValueTest
./mvnw test -Dgroups=e2e
./mvnw test -DexcludedGroups=e2e
```

Testes e execução local usam `-Duser.timezone=UTC`.

O portão antes de merge é `./mvnw verify`. Nenhum teste pode ser ignorado para
fazer o build passar.

## 15. Checklist de review

- [ ] Nenhum mock sobre porta do projeto.
- [ ] Erros afirmados por `type` e `code`, não por mensagem.
- [ ] Dados vêm de factories fluentes.
- [ ] Famílias de entrada usam teste parametrizado.
- [ ] Os dois lados de cada limite estão cobertos.
- [ ] Caminhos de falha provam que o estado não mudou.
- [ ] E2E cobre o contrato mínimo do endpoint.
- [ ] E2E lê o estado de volta do banco.
- [ ] Não existe `@Transactional` em E2E.
- [ ] Não existe `@Disabled` sem justificativa escrita.
- [ ] A suíte sem E2E roda sem Docker.
- [ ] `./mvnw verify` está verde.
