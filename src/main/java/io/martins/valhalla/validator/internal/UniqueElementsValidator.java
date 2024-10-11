package io.martins.valhalla.validator.internal;

import io.martins.valhalla.validator.UniqueElements;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UniqueElementsValidator implements ConstraintValidator<UniqueElements, Collection<?>> {

  @Override
  public boolean isValid(final Collection<?> collection, final ConstraintValidatorContext context) {
    if (collection == null || collection.size() < 2) {
      return true;
    }

    return findDuplicates(collection).isEmpty();
  }

  private List<Object> findDuplicates(final Collection<?> collection) {
    Set<Object> uniqueElements = new HashSet<>(collection.size());

    return collection.stream().filter(e -> !uniqueElements.add(e)).collect(Collectors.toList());
  }

}
