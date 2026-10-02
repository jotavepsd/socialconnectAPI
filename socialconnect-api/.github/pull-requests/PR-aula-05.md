# PR — Aula 05: API REST — CRUD, DTOs e Semântica HTTP

**Branch:** `feature/aula-05-crud-dtos-paginacao` → `main`
**Data:** 11/09/2026

## O que foi feito

**Beneficiários**
- `BeneficiarioDTO` substituído por DTOs separados: `BeneficiarioRequestDTO` (entrada), `BeneficiarioResponseDTO` (saída) e `BeneficiarioPatchDTO` (PATCH, todos os campos opcionais).
- `dataCadastro` e `idBeneficiario` agora são gerados pelo servidor. O cliente não consegue forçar esses valores.
- `BeneficiarioRepository` com métodos paginados (`findByCpf`, `findByNomeContainingIgnoreCase`) e `existsByCpf`.
- CRUD completo: `GET` paginado com filtros `nome`/`cpf` (`@PageableDefault(size = 10, sort = "nome")`), `GET` por id, `POST` (201 + Location), `PUT` (substituição total), `PATCH` (parcial), `DELETE` (204).
- Semântica de erro: 404 para ID inexistente, 409 para CPF já cadastrado (POST e PUT), 400 quando o `@Valid` falha.
- `WebConfig` com `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)` para o JSON do `Page` ter formato estável (`content` + `page`).

**Desafio (Mob Programming): CRUD de Doações**
- Migration `V3__create_doacoes_table.sql` (PK `id_doacao`, FK `fk_doacao_doador`, índices por data e tipo).
- Entity `Doacao` (`@ManyToOne` com `Doador`) e enum `TipoDoacao`.
- DTOs `DoacaoRequestDTO`, `DoacaoResponseDTO`, `DoacaoPatchDTO`.
- `DoacaoService` valida se o doador existe (404 se não existir).
- `GET /api/v1/doacoes` paginado com filtros opcionais `dataInicio`, `dataFim` e `tipo` (via `Specification`), além de `GET` por id, `POST`, `PUT`, `PATCH` e `DELETE`.

## Como testar

1. `mvn clean compile` e `mvn spring-boot:run`.
2. Beneficiários (Swagger ou curl):
   - `POST /api/v1/beneficiarios` `{"nome":"Maria da Silva","cpf":"12345678900"}` → 201; repetir o mesmo CPF → 409.
   - `GET /api/v1/beneficiarios?page=0&size=1&sort=nome,asc` → 200 com `content` e `page.totalElements`.
   - `GET /api/v1/beneficiarios?nome=mar` → filtro parcial.
   - `PATCH /api/v1/beneficiarios/1` `{"telefone":"11988887777"}` → só o telefone muda.
   - `DELETE /api/v1/beneficiarios/1` → 204; repetir → 404.
3. Doações: ainda não existe endpoint de doadores. Criar um pelo H2 Console (`/h2-console`):
   ```sql
   INSERT INTO doadores (nome, tipo, email) VALUES ('Mercado Bom Preço', 'PESSOA_JURIDICA', 'contato@bompreco.com');
   ```
   - `POST /api/v1/doacoes` `{"idDoador":1,"dataDoacao":"2026-09-11","valor":100.00,"tipo":"ALIMENTO","descricao":"Cesta básica"}` → 201.
   - `POST` com `"idDoador": 999` → 404.
   - `GET /api/v1/doacoes?dataInicio=2026-09-01&dataFim=2026-09-30&tipo=ALIMENTO` → só as doações do filtro.
   - `PATCH /api/v1/doacoes/1` `{"valor":150.50}` → 200.

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

- Os erros 404/409 usam `ResponseStatusException` por enquanto. O corpo ainda é o JSON padrão do Spring. Na Aula 06 isso vira exceções de negócio + Problem Details (RFC 7807).
- DELETE de um ID inexistente retorna 404. O estado final é o mesmo, então a operação continua idempotente.
- O POST de beneficiário é protegido contra duplicação pelo CPF único (`existsByCpf` → 409).
- Nos filtros de doação usei `Specification` em vez de um método derivado para cada combinação. Assim os três filtros são opcionais e combináveis.
