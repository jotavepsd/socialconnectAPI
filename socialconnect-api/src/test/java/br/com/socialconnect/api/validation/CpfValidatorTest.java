package br.com.socialconnect.api.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CpfValidatorTest {

    private final CpfValidator validator = new CpfValidator();

    @ParameterizedTest
    @ValueSource(strings = {"52998224725", "529.982.247-25", "11144477735"})
    @DisplayName("Deve aceitar CPFs válidos com ou sem máscara")
    void deveAceitarCpfValido(String cpf) {
        // ACT
        boolean valido = validator.isValid(cpf, null);

        // ASSERT
        Assertions.assertTrue(valido);
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678900", "111.111.111-11", "abc12345678", "5299822472", "529982247250"})
    @DisplayName("Deve recusar CPFs inválidos")
    void deveRecusarCpfInvalido(String cpf) {
        // ACT
        boolean valido = validator.isValid(cpf, null);

        // ASSERT
        Assertions.assertFalse(valido);
    }

    @Test
    @DisplayName("Deve ignorar CPF nulo (obrigatoriedade é do @NotBlank)")
    void deveIgnorarCpfNulo() {
        Assertions.assertTrue(validator.isValid(null, null));
    }
}
