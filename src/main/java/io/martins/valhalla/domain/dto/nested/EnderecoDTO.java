package io.martins.valhalla.domain.dto.nested;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(name = "EnderecoResponse")
public record EnderecoDTO(

    String cep, //

    String logradouro, //

    String complemento, //

    String bairro, //

    String localidade, //

    String uf, //

    Integer ibge, //

    LocalDateTime dataInclusao, //

    LocalDateTime dataAtualizacao //

) {

}
