# PR — Aula 04: Persistência com JPA e Migrations (Flyway)

**Branch:** `feature/aula-04-flyway-migrations` → `main`
**Data:** 04/09/2026

## O que foi feito

- Adicionado o Flyway ao projeto (`spring-boot-starter-flyway` + `flyway-database-postgresql`).
- Criada a migration `V1__create_beneficiarios_table.sql` com a PK `id_beneficiario` e o índice `idx_beneficiarios_cpf`.
- `spring.jpa.hibernate.ddl-auto` alterado de `create-drop` para `validate` (dev e test). O Hibernate agora só valida o esquema.
- Entity `Beneficiario`: atributo `id` renomeado para `idBeneficiario`, mapeado com `@Column(name = "id_beneficiario")` (regra de ouro `id_`).
- Desafio (revisão cruzada): migration `V2__create_doadores_table.sql`, Entity `Doador`, enum `TipoDoador` e `DoadorRepository`.
- Atividade registrada em `atividade-aula-04.md`.

## Como testar

1. `mvn clean compile` e `mvn spring-boot:run`.
2. Verificar no log: `Successfully applied 2 migrations to schema "PUBLIC", now at version v2`.
3. Abrir `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:socialconnectdb`, user `sa`) e rodar `SHOW TABLES;` e `SELECT * FROM flyway_schema_history;`.
4. No Swagger (`/swagger-ui.html`), fazer `POST /api/v1/beneficiarios` com um dos JSONs de `atividade-aula-04.md` → 201; depois `GET /api/v1/beneficiarios` → 200 com `idBeneficiario` preenchido.

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

- No Spring Boot 4 só o `flyway-core` não ativa a autoconfiguração do Flyway. Por isso usei o `spring-boot-starter-flyway`. O `flyway-database-postgresql` é necessário para rodar as migrations no PostgreSQL (prod / Testcontainers).
- O `DoadorRepository` ainda não é usado por nenhum Service. Ele será usado no CRUD de Doações (Aula 05).
- `mvn test` segue pendente por causa do teste com Testcontainers do projeto base (Aula 07).
