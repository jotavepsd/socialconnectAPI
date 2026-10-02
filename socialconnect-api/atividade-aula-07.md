# Atividade — Aula 07: Dojo de Testes (Endpoint de Doações)

**Disciplina:** ITE005 — Tópicos Especiais em Sistemas para Internet III
**Data:** 25/09/2026

## 1. Complementar `DoacaoServiceTest`

Arquivo: `src/test/java/br/com/socialconnect/api/doacoes/service/DoacaoServiceTest.java`

- Teste unitário puro: `@ExtendWith(MockitoExtension.class)`, `@Mock` para `DoacaoRepository` e `DoadorRepository`, `@InjectMocks` para `DoacaoService` (sem `@SpringBootTest`).
- `deveCriarDoacaoQuandoDadosValidos` (caminho feliz): mocka o `DoadorRepository.findById(1L)` e o `save`, e confere o ID e o valor. Também verifica com `Mockito.verify(doacaoRepository, Mockito.times(1)).save(...)`.
- **`deveLancarExcecaoQuandoDoadorNaoExistir`** (caminho triste pedido no dojo):
  - ARRANGE: `doadorRepository.findById(999L)` → `Optional.empty()`
  - ACT + ASSERT: `Assertions.assertThrows(RuntimeException.class, () -> doacaoService.criar(dto))`
  - ASSERT: a exceção é `RecursoNaoEncontradoException`, a mensagem cita o ID 999 e o `save` **nunca** é chamado.

> Sobre o AAA com exceção: o `assertThrows` recebe a ação como lambda. Por isso o ACT fica dentro do ASSERT, e as verificações extras (tipo, mensagem, `verify`) vêm depois.

## 2. Criar `DoacaoControllerIntegrationTest`

Arquivo: `src/test/java/br/com/socialconnect/api/doacoes/controller/DoacaoControllerIntegrationTest.java`

Estrutura usada (adaptada ao Spring Boot 4 / Testcontainers 2):

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureTestRestTemplate
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class DoacaoControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @Autowired
    private TestRestTemplate restTemplate;
    ...
}
```

| Cenário | Entrada | Esperado | Resultado |
|---------|---------|----------|-----------|
| `deveCriarDoacaoQuandoDadosValidos` | POST com doador existente e data de hoje | `201 Created`, `idDoacao` não nulo, cabeçalho `Location` | ✅ |
| `deveRetornar400QuandoDataFutura` | POST com `dataDoacao = LocalDate.now().plusDays(10)` | `400 Bad Request` em Problem Details, mensagem com "futuro", campo `dataDoacao` | ✅ |

### Diferenças em relação ao enunciado (Spring Boot 4.1 / Testcontainers 2)

- `PostgreSQLContainer` agora fica em `org.testcontainers.postgresql` e não é mais genérico (`PostgreSQLContainer<?>` → `PostgreSQLContainer`).
- `TestRestTemplate` foi para o módulo `spring-boot-resttestclient` e precisa de `@AutoConfigureTestRestTemplate` (e de `spring-boot-restclient` no classpath de teste).
- Usei `@ServiceConnection` no container para o Spring apontar o datasource para o PostgreSQL do Testcontainers automaticamente.
- Tirei o `spring.jpa.database-platform=H2Dialect` fixo do `application.properties`. Com ele, o Hibernate usaria o dialeto do H2 contra o PostgreSQL. Agora o dialeto é detectado sozinho.

## TDD — `@DataNaoFutura`

1. **RED:** criei o `DataNaoFuturaValidatorTest` (hoje ✅, passado ✅, futuro ❌, nulo ✅) antes do validador existir. O teste não compilava/falhava.
2. **GREEN:** implementei a anotação `@DataNaoFutura` + `DataNaoFuturaValidator` e apliquei em `dataDoacao` nos DTOs de Doação.
3. **REFACTOR:** mensagem movida para o `messages.properties` (`DataNaoFutura.dataDoacao=A data da doação não pode estar no futuro`) sem quebrar os testes.

## Outros testes adicionados

- `BeneficiarioServiceTest`: criação com CPF normalizado e `dataCadastro` = hoje (com `ArgumentCaptor`), CPF duplicado → `CpfDuplicadoException`, PATCH altera só os campos enviados, DELETE de ID inexistente → 404.
- `CpfValidatorTest`: CPFs válidos com e sem máscara; inválidos por dígito verificador, sequência repetida, letras e tamanho errado.

## Checklist de Sucesso

- [x] O comando `mvn test` executa com **BUILD SUCCESS** (24 testes, 0 falhas).
- [x] Os testes seguem o padrão AAA com comentários `// ARRANGE`, `// ACT`, `// ASSERT`.
- [x] O teste de integração usa o Testcontainers (log do Docker: container `postgres:17` iniciado, Flyway aplicou V1–V3).
- [x] Nomes dos métodos são descritivos (ex.: `deveRetornar400QuandoDataFutura`).

```
[INFO] Tests run: 24, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
