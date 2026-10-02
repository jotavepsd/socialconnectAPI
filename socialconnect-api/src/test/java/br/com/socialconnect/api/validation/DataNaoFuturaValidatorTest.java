package br.com.socialconnect.api.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class DataNaoFuturaValidatorTest {

    private final DataNaoFuturaValidator validator = new DataNaoFuturaValidator();

    @Test
    @DisplayName("Deve aceitar data de hoje")
    void deveAceitarDataDeHoje() {
        // ARRANGE
        LocalDate hoje = LocalDate.now();

        // ACT
        boolean valido = validator.isValid(hoje, null);

        // ASSERT
        Assertions.assertTrue(valido);
    }

    @Test
    @DisplayName("Deve aceitar data no passado")
    void deveAceitarDataNoPassado() {
        // ARRANGE
        LocalDate ontem = LocalDate.now().minusDays(1);

        // ACT
        boolean valido = validator.isValid(ontem, null);

        // ASSERT
        Assertions.assertTrue(valido);
    }

    @Test
    @DisplayName("Deve recusar data no futuro")
    void deveRecusarDataNoFuturo() {
        // ARRANGE
        LocalDate futuro = LocalDate.now().plusDays(10);

        // ACT
        boolean valido = validator.isValid(futuro, null);

        // ASSERT
        Assertions.assertFalse(valido);
    }

    @Test
    @DisplayName("Deve ignorar data nula (obrigatoriedade é do @NotNull)")
    void deveIgnorarDataNula() {
        // ARRANGE / ACT
        boolean valido = validator.isValid(null, null);

        // ASSERT
        Assertions.assertTrue(valido);
    }
}
