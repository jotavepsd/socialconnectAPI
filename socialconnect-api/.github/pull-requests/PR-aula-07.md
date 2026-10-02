# PR — Aula 07: Testes Automatizados e Qualidade

**Branch:** `feature/aula-07-testes-automatizados` → `main`
**Data:** 25/09/2026

## O que foi feito

- **TDD de `@DataNaoFutura`:** teste do validador escrito primeiro (RED). Depois, anotação + `DataNaoFuturaValidator` aplicados em `dataDoacao` (GREEN) e mensagem movida para o `messages.properties` (REFACTOR).
- **Testes unitários** (`@ExtendWith(MockitoExtension.class)`, sem subir contexto Spring, padrão AAA comentado):
  - `DoacaoServiceTest`: sucesso, `deveLancarExcecaoQuandoDoadorNaoExistir` (desafio do dojo) e busca de doação inexistente.
  - `BeneficiarioServiceTest`: criação com CPF normalizado, CPF duplicado, PATCH parcial e DELETE de ID inexistente.
  - `CpfValidatorTest` e `DataNaoFuturaValidatorTest`.
- **Teste de integração** `DoacaoControllerIntegrationTest` com `@SpringBootTest(RANDOM_PORT)` + Testcontainers (`postgres:17`, `@ServiceConnection`):
  - POST válido → 201 com `idDoacao`;
  - POST com data futura → 400 em Problem Details citando "futuro".
- **Build/config:**
  - dependências `spring-boot-resttestclient` e `spring-boot-restclient` (test);
  - removido o dialeto H2 fixo do `application.properties`, para o Hibernate detectar o PostgreSQL;
  - imagem do `TestcontainersConfiguration` fixada em `postgres:17`.
- Atividade do dojo registrada em `atividade-aula-07.md`.

## Como testar

1. Garantir que o Docker está rodando (`docker info`).
2. `mvn clean test`.
3. Esperado: `Tests run: 24, Failures: 0, Errors: 0, Skipped: 0` e `BUILD SUCCESS`. O log mostra o container `postgres:17` e o Flyway aplicando V1–V3.
4. Opcional: `mvn test -Dtest='*ServiceTest,*ValidatorTest'` roda só os unitários (sem Docker), em menos de 1 s.

## Checklist de Qualidade

- [x] Código compila sem erros (`mvn clean compile`)
- [x] Testes passam (`mvn test`)
- [x] Arquitetura em camadas respeitada (Controller → Service → Repository)
- [x] DTOs separados da Entity
- [x] Injeção de dependência via construtor (sem `@Autowired` em atributo)
- [x] Commits atômicos e descritivos (Conventional Commits)
- [x] `AI_USAGE.md` atualizado

## Uso de IA

- [ ] Não usei IA nesta entrega
- [x] Usei IA — detalhes registrados no `AI_USAGE.md`

## Observações

- No código de produção não há `@Autowired` em atributo. No `DoacaoControllerIntegrationTest` mantive `@Autowired` nos campos `TestRestTemplate` e `DoadorRepository`, porque essa é a estrutura obrigatória do dojo (o JUnit instancia a classe de teste).
- O commit "RED" do TDD (`test: escreve testes do validador de data nao futura`) propositalmente não compila, porque o validador ainda não existia. O commit seguinte deixa o build verde.
- Adaptações para Spring Boot 4 / Testcontainers 2 estão descritas em `atividade-aula-07.md`.
- Os testes de integração exigem Docker. Sem ele, `ApiApplicationTests` e `DoacaoControllerIntegrationTest` falham ao subir o container.
