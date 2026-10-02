# PR — Correção da busca de beneficiários (GET) pelo Swagger

**Branch:** `fix/busca-beneficiarios-swagger` → `main`
**Data:** 25/09/2026

## O que foi feito

- Aplicada a correção enviada pelo professor no `GET /api/v1/beneficiarios`:
  - O `Pageable` deixa de ser recebido direto e vira três parâmetros simples: `page` (padrão `0`), `size` (padrão `10`) e `sort` (padrão `idBeneficiario,asc`).
  - O `sort` é sanitizado (remove colchetes, aspas e espaços). Assim o formato que o Swagger UI envia (`["nome,desc"]`) passa a funcionar.
  - O `Pageable` é montado manualmente com `PageRequest.of(page, size, Sort.by(direcao, campo))`.
- `GlobalExceptionHandler`: ordenação por campo inexistente (`PropertyReferenceException`) agora retorna **400** em Problem Details, e não mais 500.
- Nova mensagem `erro.ordenacao.invalida` no `messages.properties`.
- Parâmetros `page`, `size` e `sort` documentados com `@Parameter` e exemplos.

## Como testar

1. `mvn spring-boot:run` e abrir `http://localhost:8080/swagger-ui.html`.
2. Cadastrar dois beneficiários (ex.: CPFs `52998224725` e `11144477735`).
3. Em `GET /api/v1/beneficiarios`, clicar em "Try it out" e "Execute" sem alterar nada → 200, ordenado por `idBeneficiario`.
4. Testar `sort` = `nome,desc` e também `["nome,desc"]` → 200, ordenado por nome decrescente.
5. `page=1&size=1` → segunda página com 1 registro.
6. `sort=string` → 400 `"Não é possível ordenar pelo campo 'string'."`.

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

- **Causa do bug:** com `Pageable` como parâmetro, o Swagger UI mostrava um JSON de exemplo (`{"page":0,"size":1,"sort":["string"]}`) e enviava `sort=["string"]`. O Spring Data tentava ordenar por uma propriedade inexistente e a requisição falhava.
- A ordenação padrão mudou de `nome` para `idBeneficiario,asc`, como no código do professor.
- No Spring Data 4 a `PropertyReferenceException` fica em `org.springframework.data.core` (antes era `org.springframework.data.mapping`).
