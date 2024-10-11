package io.martins.valhalla.validator;

import io.martins.valhalla.validator.internal.EnumerationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Constraint(validatedBy = EnumerationValidator.class)
public @interface Enumeration {

  Class<? extends Enum<?>> enumClass();

  String message() default "deve ser um valor do enum {enumClass}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
