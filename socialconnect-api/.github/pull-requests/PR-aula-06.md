# PR — Aula 06: Validação, Erros (RFC 7807) e Swagger

**Branch:** `feature/aula-06-validacao-erros-swagger` → `main`
**Data:** 14/09/2026

## O que foi feito

- **Bean Validation:** regras nos DTOs de entrada de Beneficiário e Doação (`@NotBlank`, `@NotNull`, `@Size`, `@Positive`) e `@Valid` em POST, PUT e PATCH.
- **Validação customizada `@CPF`:** `CpfValidator` com dígitos verificadores, aceitando CPF com ou sem máscara. O Service grava o CPF normalizado (só dígitos).
- **Problem Details (RFC 7807):** record `ProblemDetail` e `GlobalExceptionHandler` (400 validação/JSON/parâmetro, 404, 409, 405/415 do Spring, 500 genérico sem stack trace).
- **Exceções de negócio:** `RecursoNaoEncontradoException` e `CpfDuplicadoException` substituem os `ResponseStatusException` da Aula 05.
- **i18n:** `messages.properties` + `I18nConfig` (`MessageSource` UTF-8, locale fixo pt-BR). Mensagens de validação e de erro em português.
- **SpringDoc OpenAPI:** `@Tag`, `@Operation`, `@ApiResponse` e `@Parameter` nos controllers; `@Schema` com exemplos nos DTOs.
- Atividade "Caça a Defeitos" registrada em `atividade-aula-06.md`.

## Como testar

1. `mvn clean compile` e `mvn spring-boot:run`.
2. Abrir `http://localhost:8080/swagger-ui.html`. As tags "Beneficiários" e "Doações" devem aparecer com exemplos preenchidos e os status 200/201/204/400/404/409 documentados.
3. `POST /api/v1/beneficiarios` com o exemplo do Swagger (`cpf` `52998224725`) → 201. Repetir → 409 em Problem Details.
4. `POST` com `{"nome":"","cpf":"12345678900"}` → 400 com `errors` listando `nome` e `cpf` em português.
5. `GET /api/v1/beneficiarios/999` → 404; `GET /api/v1/beneficiarios/abc` → 400.
6. `POST /api/v1/doacoes` com `"tipo":"XYZ"` → 400; com `"valor":-5` → 400 "Valor deve ser maior que zero".

## Checklist de Qualidade

- [x] Código compila sem erros (`mvn clean compile`)
- [ ] Testes passam (`mvn test`)
- [x] Arquitetura em camadas respeitada (Controller → Service → Repository)
- [x] DTOs separados da Entity
- [x] Injeção de dependência via construtor (sem `@Autowired` em atributo)
- [x] Commits atômicos e descritivos (Conventional Commits)
- [x] `AI_USAGE.md` atualizado

## Uso de IA

- [ ] Não usei IA nesta entrega
- [x] Usei IA — detalhes registrados no `AI_USAGE.md`

## Observações

- O record se chama `ProblemDetail`, como no material da aula. Ele fica em `br.com.socialconnect.api.exception` e não deve ser confundido com o `org.springframework.http.ProblemDetail`.
- As exceções do Spring MVC (método não suportado, rota inexistente etc.) não viram 500: o handler genérico mantém o status original delas.
- Os exemplos de CPF no Swagger usam um CPF válido pelo dígito verificador (`52998224725`), senão o próprio exemplo falharia na validação.
- A suíte de testes (`mvn test`) fica para a Aula 07.
