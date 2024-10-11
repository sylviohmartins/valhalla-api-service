package io.martins.valhalla.domain.vo;

import io.martins.valhalla.domain.enumeration.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FiltroPessoaRequest")
public record FiltroPessoaVO(

    String cpf, //

    String nome, //

    StatusEnum status //

) {

}
