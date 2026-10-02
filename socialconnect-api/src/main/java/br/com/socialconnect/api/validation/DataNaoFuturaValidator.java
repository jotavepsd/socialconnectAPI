package br.com.socialconnect.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DataNaoFuturaValidator implements ConstraintValidator<DataNaoFutura, LocalDate> {

    @Override
    public boolean isValid(LocalDate data, ConstraintValidatorContext context) {
        if (data == null) return true; // @NotNull cuida da obrigatoriedade
        return !data.isAfter(LocalDate.now());
    }
}
