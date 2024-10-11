package io.martins.valhalla.validator.internal;

import io.martins.valhalla.validator.Enumeration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Stream;

public class EnumerationValidator implements ConstraintValidator<Enumeration, CharSequence> {

  private List<String> acceptedValues;

  @Override
  public void initialize(final Enumeration annotation) {
    acceptedValues = Stream.of(annotation.enumClass().getEnumConstants()).map(Enum::name).toList();
  }

  @Override
  public boolean isValid(final CharSequence value, final ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    return acceptedValues.contains(value.toString().toUpperCase());
  }

}
