package io.martins.valhalla.validator;

import io.martins.valhalla.validator.internal.CpfValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@Constraint(validatedBy = CpfValidator.class)
public @interface Cpf {

  String message() default "CPF inválido";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
