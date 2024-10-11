package io.martins.valhalla.domain.dto;

import io.martins.valhalla.domain.dto.nested.EnderecoDTO;
import io.martins.valhalla.domain.enumeration.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "PessoaResponse")
public record PessoaDTO(

    Long id, //

    Long iid, //

    String cpf, //

    String nome, //

    Integer versao, //

    StatusEnum status, //

    List<EnderecoDTO> enderecos, //

    LocalDateTime dataCriacao, //

    LocalDateTime dataAtualizacao //

) {

}
