package br.com.socialconnect.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DataNaoFuturaValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataNaoFutura {
    String message() default "{DataNaoFutura.data}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
