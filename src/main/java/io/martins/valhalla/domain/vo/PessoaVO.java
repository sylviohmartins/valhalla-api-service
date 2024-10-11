package io.martins.valhalla.domain.vo;

import io.martins.valhalla.domain.enumeration.StatusEnum;
import io.martins.valhalla.domain.vo.nested.EnderecoVO;
import io.martins.valhalla.validator.Cpf;
import io.martins.valhalla.validator.UniqueElements;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "PessoaRequest")
public record PessoaVO(

    Long id, //

    Long iid, //

    @NotBlank @Cpf String cpf, //

    @NotBlank @Size(max = 50) String nome, //

    Integer versao, //

    StatusEnum status, //

    @UniqueElements(message = "{UniqueElements.enderecos}") List<EnderecoVO> enderecos, //

    LocalDateTime dataCriacao, //

    LocalDateTime dataAtualizacao //

) {

}
