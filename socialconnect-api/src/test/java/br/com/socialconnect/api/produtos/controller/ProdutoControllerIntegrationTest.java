package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureTestRestTemplate
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ProdutoControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("POST /api/v1/produtos com dados válidos deve retornar 201 Created")
    void deveCriarProdutoQuandoDadosValidos() {
        // ARRANGE
        ProdutoRequestDTO dto = new ProdutoRequestDTO("Arroz 5kg", CategoriaProduto.ALIMENTO, 3, 10, "unidade");

        // ACT
        ResponseEntity<ProdutoResponseDTO> resposta =
                restTemplate.postForEntity("/api/v1/produtos", dto, ProdutoResponseDTO.class);

        // ASSERT
        Assertions.assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        Assertions.assertNotNull(resposta.getBody());
        Assertions.assertNotNull(resposta.getBody().idProduto());
        Assertions.assertTrue(resposta.getBody().estoqueBaixo());
        Assertions.assertNotNull(resposta.getHeaders().getLocation());
    }

    @Test
    @DisplayName("POST /api/v1/produtos com nome duplicado deve retornar 409 Conflict")
    void deveRetornar409QuandoNomeDuplicado() {
        // ARRANGE
        ProdutoRequestDTO dto = new ProdutoRequestDTO("Feijão 1kg", CategoriaProduto.ALIMENTO, 5, 10, "unidade");
        restTemplate.postForEntity("/api/v1/produtos", dto, ProdutoResponseDTO.class);

        // ACT
        ResponseEntity<String> resposta =
                restTemplate.postForEntity("/api/v1/produtos", dto, String.class);

        // ASSERT
        Assertions.assertEquals(HttpStatus.CONFLICT, resposta.getStatusCode());
        Assertions.assertNotNull(resposta.getBody());
        Assertions.assertTrue(resposta.getBody().contains("Produto já cadastrado"));
    }

    @Test
    @DisplayName("POST /api/v1/produtos com estoque negativo deve retornar 422 Unprocessable Entity")
    void deveRetornar422QuandoEstoqueNegativo() {
        // ARRANGE
        ProdutoRequestDTO dto = new ProdutoRequestDTO("Produto Negativo", CategoriaProduto.OUTROS, -1, 0, "unidade");

        // ACT
        ResponseEntity<String> resposta =
                restTemplate.postForEntity("/api/v1/produtos", dto, String.class);

        // ASSERT
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, resposta.getStatusCode());
        Assertions.assertNotNull(resposta.getBody());
        Assertions.assertTrue(resposta.getBody().contains("estoque"));
    }
}
