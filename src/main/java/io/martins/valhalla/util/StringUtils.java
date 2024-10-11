package io.martins.valhalla.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RegExUtils;

@UtilityClass
public class StringUtils extends org.apache.commons.lang3.StringUtils {

  public String removeNonDigits(final String str) {
    return RegExUtils.replaceAll(str, "\\D", "");
  }

}
