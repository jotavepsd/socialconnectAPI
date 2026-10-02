package br.com.socialconnect.api.doacoes.controller;

import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doadores.model.Doador;
import br.com.socialconnect.api.doadores.model.TipoDoador;
import br.com.socialconnect.api.doadores.repository.DoadorRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureTestRestTemplate
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class DoacaoControllerIntegrationTest {

    // PostgreSQL 17 efêmero; @ServiceConnection liga o datasource do Spring a este container
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    // Em testes o Spring injeta os beans no campo; não há construtor para usar aqui
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DoadorRepository doadorRepository;

    @Test
    @DisplayName("POST /api/v1/doacoes com dados válidos deve retornar 201 Created")
    void deveCriarDoacaoQuandoDadosValidos() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        Doador doador = doadorRepository.save(Doador.builder()
                .nome("Mercado Bom Preço").tipo(TipoDoador.PESSOA_JURIDICA).email("contato@bompreco.com")
                .build());
        DoacaoRequestDTO dto = new DoacaoRequestDTO(
                doador.getIdDoador(), LocalDate.now(), new BigDecimal("100.00"),
                TipoDoacao.ALIMENTO, "10 cestas básicas"
        );

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        ResponseEntity<DoacaoResponseDTO> resposta =
                restTemplate.postForEntity("/api/v1/doacoes", dto, DoacaoResponseDTO.class);

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        Assertions.assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        Assertions.assertNotNull(resposta.getBody());
        Assertions.assertNotNull(resposta.getBody().idDoacao(), "ID da doação não deve ser nulo");
        Assertions.assertNotNull(resposta.getHeaders().getLocation());
    }

    @Test
    @DisplayName("POST /api/v1/doacoes com data no futuro deve retornar 400 com Problem Details")
    void deveRetornar400QuandoDataFutura() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        Doador doador = doadorRepository.save(Doador.builder()
                .nome("Maria Doadora").tipo(TipoDoador.PESSOA_FISICA)
                .build());
        DoacaoRequestDTO dto = new DoacaoRequestDTO(
                doador.getIdDoador(), LocalDate.now().plusDays(10), new BigDecimal("50.00"),
                TipoDoacao.ALIMENTO, "Data inválida"
        );

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        ResponseEntity<String> resposta = restTemplate.postForEntity("/api/v1/doacoes", dto, String.class);

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        Assertions.assertNotNull(resposta.getBody());
        Assertions.assertTrue(resposta.getBody().contains("futuro"), "Mensagem deve citar a data no futuro");
        Assertions.assertTrue(resposta.getBody().contains("\"field\":\"dataDoacao\""));
    }
}
