package br.com.socialconnect.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = EstoqueNaoNegativoValidator.class)
@Target({FIELD, METHOD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface EstoqueNaoNegativo {
    String message() default "Estoque não pode ser negativo";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
