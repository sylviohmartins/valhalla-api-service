package io.martins.valhalla.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageConstants {

  public static final String ALREADY_EXISTS = "io.martins.ALREADY_EXISTS";

  public static final String INVALID_FIELDS = "io.martins.INVALID_FIELDS";

  public static final String RECORD_NOT_FOUND = "io.martins.RECORD_NOT_FOUND";

  // CUSTOM

  public static final String CPF_JA_EXISTENTE = "io.martins.CPF_JA_EXISTENTE";

  public static final String ENDERECO_NAO_ENCONTRADO_PARA_CEP_INFORMADO = "io.martins.ENDERECO_NAO_ENCONTRADO_PARA_CEP_INFORMADO";

  public static final String ERRO_SERVIDOR_VIACEP = "io.martins.ERRO_SERVIDOR_VIACEP";

}
