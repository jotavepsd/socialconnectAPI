# SocialConnect API

> API RESTful de gestão para instituições sociais (ONGs, bancos de alimentos,
> CRAS, abrigos). Conecta doadores, voluntários e beneficiários.

**Disciplina:** ITE005 — Tópicos Especiais em Sistemas para Internet III
**Stack:** Java 21 · Spring Boot 4.1.1 · JPA · H2 (dev) · PostgreSQL (prod)

---

## Como Rodar

### Pré-requisitos

- JDK 21 LTS ([Adoptium](https://adoptium.net/))
- Maven 3.9+ (ou use o wrapper: `./mvnw`)
- IDE: IntelliJ IDEA (recomendado) ou VS Code

### Passos

```bash
# 1. Clone o repositório
git clone <url-do-repo>
cd socialconnect-api

# 2. Compile o projeto
mvn clean compile

# 3. Rode a aplicação
mvn spring-boot:run
## Produtos — Avaliação A1

O módulo de produtos está disponível em `/api/v1/produtos` e contempla:

- CRUD de produtos;
- paginação e ordenação;
- filtros por nome parcial e categoria;
- validação de dados com Bean Validation e validação customizada;
- regra de estoque baixo (`estoqueBaixo`);
- unicidade de nome com resposta `409 Conflict`;
- estoque negativo com resposta `422 Unprocessable Entity`;
- documentação completa no Swagger/OpenAPI.

### Principais endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/v1/produtos` | Lista produtos com paginação/filtros |
| GET | `/api/v1/produtos/{idProduto}` | Busca produto por ID |
| POST | `/api/v1/produtos` | Cadastra produto |
| PUT | `/api/v1/produtos/{idProduto}` | Atualiza produto |
| DELETE | `/api/v1/produtos/{idProduto}` | Remove produto |

### Swagger

Com a aplicação em execução, acesse:

`http://localhost:8080/swagger-ui.html`

### Testes

Os testes do módulo incluem testes unitários do `ProdutoService` e testes de integração do `ProdutoController` com PostgreSQL 17 via Testcontainers.
