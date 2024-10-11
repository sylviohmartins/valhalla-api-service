package io.martins.valhalla.domain.vo.nested;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "EnderecoRequest")
public record EnderecoVO(

    Long id, //

    @NotBlank
    String cep, //

    String logradouro, //

    String complemento, //

    String bairro, //

    String localidade, //

    String uf, //

    Integer ibge //

) {

}
