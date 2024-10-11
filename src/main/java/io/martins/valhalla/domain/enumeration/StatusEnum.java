package io.martins.valhalla.domain.enumeration;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum StatusEnum {

  ATIVO(1),
  INATIVO(2),
  EXCLUIDO(3);

  private static final Map<Integer, StatusEnum> IDS = Stream.of(values()).collect(Collectors.toMap(StatusEnum::getId, v -> v));

  private final Integer id;

  StatusEnum(final Integer id) {
    this.id = id;
  }

  //	@JsonValue
  public static StatusEnum valueOfId(final Integer id) {
    return IDS.getOrDefault(id, null);
  }

  public static StatusEnum valueOfName(final String name) {
    return Stream.of(values()).filter(v -> v.toString().toLowerCase().equalsIgnoreCase(name)).findFirst().orElse(null);
  }

}
