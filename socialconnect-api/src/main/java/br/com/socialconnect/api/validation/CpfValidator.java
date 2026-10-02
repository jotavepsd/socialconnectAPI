package br.com.socialconnect.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<CPF, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.isBlank()) return true; // @NotBlank cuida da obrigatoriedade

        // Aceita só dígitos, com ou sem máscara (000.000.000-00)
        if (!cpf.matches("[\\d.\\-]+")) return false;

        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() != 11) return false;

        // Sequências repetidas (111.111.111-11) passam no cálculo, mas são inválidas
        if (digitos.chars().distinct().count() == 1) return false;

        return digitoVerificador(digitos, 9) == digitos.charAt(9) - '0'
                && digitoVerificador(digitos, 10) == digitos.charAt(10) - '0';
    }

    // Calcula o dígito verificador usando os 'tamanho' primeiros dígitos
    private int digitoVerificador(String digitos, int tamanho) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += (digitos.charAt(i) - '0') * (tamanho + 1 - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
