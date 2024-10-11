package io.martins.valhalla.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import io.martins.valhalla.validator.UniqueElements.List;
import io.martins.valhalla.validator.internal.UniqueElementsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = UniqueElementsValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(List.class)
public @interface UniqueElements {

  String message() default "Não pode haver elementos duplicados na lista";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Target({TYPE})
  @Retention(RUNTIME)
  @Documented
  @interface List {

    UniqueElements[] value();
  }

}
