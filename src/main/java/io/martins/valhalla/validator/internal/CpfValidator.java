package io.martins.valhalla.validator.internal;

import io.martins.valhalla.validator.Cpf;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.RegExUtils;

public class CpfValidator implements ConstraintValidator<Cpf, String> {

  private static final int[] MULTIPLIERS = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

  @Override
  public boolean isValid(final String cpf, final ConstraintValidatorContext context) {
    String onlyDigits = RegExUtils.replaceAll(cpf, "\\D", "");

    if ("".equals(onlyDigits)) {
      return true;
    }

    if (onlyDigits.length() != 11 //
        || "00000000000".equals(onlyDigits) //
        || "11111111111".equals(onlyDigits) //
        || "22222222222".equals(onlyDigits) //
        || "33333333333".equals(onlyDigits) //
        || "44444444444".equals(onlyDigits) //
        || "55555555555".equals(onlyDigits) //
        || "66666666666".equals(onlyDigits) //
        || "77777777777".equals(onlyDigits) //
        || "88888888888".equals(onlyDigits) //
        || "99999999999".equals(onlyDigits)) {
      return false;
    }

    int digit1 = calculateDigit(onlyDigits.substring(0, 9));
    int digit2 = calculateDigit(onlyDigits.substring(0, 9) + digit1);

    return (onlyDigits.substring(0, 9) + digit1 + digit2).equals(onlyDigits);
  }

  private int calculateDigit(final String str) {
    int sum = 0;
    int digit;

    for (int index = str.length() - 1; index >= 0; index--) {
      digit = Integer.parseInt(str.substring(index, index + 1));
      sum += digit * MULTIPLIERS[MULTIPLIERS.length - str.length() + index];
    }

    sum = 11 - sum % 11;

    return sum > 9 ? 0 : sum;
  }

}
