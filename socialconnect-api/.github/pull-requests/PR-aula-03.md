# PR — Aula 03: Arquitetura em Camadas (Beneficiários)

**Branch:** `feature/aula-03-arquitetura-camadas` → `main`
**Data:** 04/09/2026

## O que foi feito

- Criado o `BeneficiarioDTO` como `record` (Java 21) para não expor a Entity na API.
- Criado o `BeneficiarioService` com a regra de negócio e as conversões Entity ↔ DTO (`toDTO` / `toEntity`).
- Criado o `BeneficiarioController` (`/api/v1/beneficiarios`) com semântica HTTP via `ResponseEntity`:
  - `GET /api/v1/beneficiarios` → 200 OK
  - `GET /api/v1/beneficiarios/{idBeneficiario}` → 200 OK
  - `POST /api/v1/beneficiarios` → 201 Created + cabeçalho `Location`
  - `DELETE /api/v1/beneficiarios/{idBeneficiario}` → 204 No Content
- Injeção de dependência via construtor com campos `final` em todas as camadas.

## Como testar

1. `mvn clean compile` e depois `mvn spring-boot:run`.
2. Acessar `http://localhost:8080/swagger-ui.html` e fazer um `POST /api/v1/beneficiarios` com:
   ```json
   {
     "nome": "Maria da Silva",
     "cpf": "12345678900",
     "telefone": "11999999999",
     "endereco": "Rua das Flores, 123",
     "situacaoVulnerabilidade": "Renda familiar baixa",
     "dataCadastro": "2026-09-04"
   }
   ```
   Verificar `201 Created` e o cabeçalho `Location: /api/v1/beneficiarios/1`.
3. `GET /api/v1/beneficiarios` e `GET /api/v1/beneficiarios/1` devem retornar 200; `DELETE /api/v1/beneficiarios/1` deve retornar 204.

## Checklist de Qualidade

- [x] Código compila sem erros (`mvn clean compile`)
- [ ] Testes passam (`mvn test`)
- [x] Arquitetura em camadas respeitada (Controller → Service → Repository)
- [x] DTOs separados da Entity
- [x] Injeção de dependência via construtor (sem `@Autowired` em atributo)
- [x] Commits atômicos e descritivos (Conventional Commits)
- [x] `AI_USAGE.md` atualizado

## Uso de IA

- [x] Não usei IA nesta entrega
- [ ] Usei IA — detalhes registrados no `AI_USAGE.md`

## Observações

- A Entity ainda usa o atributo genérico `id` (conforme o projeto base). A troca para `idBeneficiario` / coluna `id_beneficiario` fica para a Aula 04, junto com o Flyway.
- `buscarPorId` ainda lança `RuntimeException` quando o ID não existe (vira 500). O tratamento correto (404 com Problem Details) será feito na Aula 06.
- `mvn test` não foi marcado: o `ApiApplicationTests` do projeto base depende de Docker (Testcontainers); a suíte de testes será trabalhada na Aula 07.
