# Atividade — Aula 06: Caça a Defeitos

**Disciplina:** ITE005 — Tópicos Especiais em Sistemas para Internet III
**Data:** 14/09/2026
**Endpoint analisado:** `POST /api/v1/beneficiarios` (estado do código ao fim da Aula 05)

## Lista de problemas encontrados

| # | Problema | Como reproduzir (antes) | Impacto |
|---|----------|-------------------------|---------|
| 1 | Falta validação real de CPF. O `@Pattern("\\d{11}\|\\d{14}")` só confere a quantidade de dígitos. | `{"nome":"Y","cpf":"12345678900"}` → 201 Created | CPF inexistente gravado no banco |
| 2 | Com máscara ou letras o CPF é recusado pelo motivo errado, e o mesmo CPF pode entrar duas vezes (com e sem máscara) | `"cpf":"529.982.247-25"` → 400; `"cpf":"abc12345678"` → 400 genérico | Mensagem ruim para o usuário e risco de duplicidade |
| 3 | Erro retorna JSON genérico do Spring (não segue a RFC 7807) | `POST` com CPF repetido → `{"timestamp":…,"status":409,"error":"Conflict","path":…}` | Front-end não sabe o motivo nem quais campos estão errados |
| 4 | Erros de validação não listam os campos | `{"nome":"","cpf":"abc"}` → 400 sem detalhes | Cliente não sabe o que corrigir |
| 5 | Mensagens fixas no código e sem i18n; erros do Spring MVC em inglês | `PUT /api/v1/beneficiarios` → `"Method 'PUT' is not supported."` | Mensagens inconsistentes para o usuário brasileiro |
| 6 | JSON com enum inválido e `id` não numérico caem no tratamento padrão | `{"tipo":"XYZ"}` em `/doacoes`, `GET /beneficiarios/abc` | Resposta sem padrão |
| 7 | Swagger sem exemplos de requisição | Swagger UI → "Example Value" com `"string"` em todos os campos | Difícil testar a API |
| 8 | Falta documentação de parâmetros e status codes | Swagger lista só "200 OK" em todos os endpoints | Contrato incompleto (nota reduzida na A1) |
| 9 | `PATCH` sem `@Valid` | `PATCH` com `nome` de 300 caracteres → erro 500 do banco | Estoura o tamanho da coluna |

## Correções aplicadas

1. **Validação de CPF customizada:** anotação `@CPF` + `CpfValidator` (`br.com.socialconnect.api.validation`). O validador:
   - aceita só dígitos, com ou sem máscara (letras → inválido);
   - exige 11 dígitos e recusa sequências repetidas (`111.111.111-11`);
   - confere os dois dígitos verificadores.
2. **CPF normalizado no Service:** o CPF é gravado só com dígitos, então a checagem `existsByCpf` não depende da máscara.
3. **Problem Details (RFC 7807):** record `ProblemDetail` (`type`, `title`, `status`, `detail`, `instance`, `timestamp`, `errors`) e `GlobalExceptionHandler` com `@RestControllerAdvice`:
   - `MethodArgumentNotValidException` → 400 com a lista `errors` (`field` + `message`)
   - `HttpMessageNotReadableException` / `MethodArgumentTypeMismatchException` → 400
   - `RecursoNaoEncontradoException` → 404
   - `CpfDuplicadoException` → 409
   - exceções do Spring MVC (405, 415…) mantêm o status, com mensagem em português
   - qualquer outra → 500 com mensagem genérica (sem stack trace)
4. **Exceções de negócio:** os Services trocaram `ResponseStatusException` por `RecursoNaoEncontradoException` e `CpfDuplicadoException`.
5. **i18n:** `messages.properties` + `I18nConfig` (`MessageSource` UTF-8 e `FixedLocaleResolver` pt-BR). Os DTOs usam chaves (`{NotBlank.nome}`, `{CPF.invalido}`…).
6. **Swagger:** `@Tag`, `@Operation`, `@ApiResponse` (200/201/204/400/404/409 com schema `ProblemDetail`) e `@Parameter` nos controllers; `@Schema(description, example)` em todos os DTOs; `@ParameterObject` no `Pageable`.
7. **`@Valid` também no PATCH**, com `@Size` nos campos do `PatchDTO`.

## Depois das correções

```
POST /api/v1/beneficiarios {"nome":"","cpf":"abc12345678"}
HTTP 400
{
  "type": "https://socialconnect.api/errors/validacao",
  "title": "Erro de validação",
  "status": 400,
  "detail": "Um ou mais campos são inválidos.",
  "instance": "/api/v1/beneficiarios",
  "timestamp": "2026-09-14T20:41:12.52",
  "errors": [
    { "field": "cpf",  "message": "CPF inválido" },
    { "field": "nome", "message": "Nome é obrigatório" }
  ]
}
```

```
POST /api/v1/beneficiarios {"nome":"X","cpf":"529.982.247-25"}   (CPF já cadastrado)
HTTP 409
{
  "type": "https://socialconnect.api/errors/cpf-duplicado",
  "title": "CPF já cadastrado",
  "status": 409,
  "detail": "O CPF 52998224725 já está cadastrado no sistema.",
  ...
}
```

## Apresentação (2 min)

- **Maior defeito:** o `@Pattern` passava a impressão de validar CPF, mas aceitava qualquer sequência de 11 dígitos.
- **Lição:** o padrão de erro (RFC 7807) é parte do contrato da API, igual ao formato de sucesso. Por isso ele também aparece documentado no Swagger.
