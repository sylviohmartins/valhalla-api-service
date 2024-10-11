package io.martins.valhalla.controller;

import io.martins.valhalla.domain.dto.PessoaDTO;
import io.martins.valhalla.domain.entity.Pessoa;
import io.martins.valhalla.domain.mapper.PessoaMapper;
import io.martins.valhalla.domain.vo.FiltroPessoaVO;
import io.martins.valhalla.domain.vo.PessoaVO;
import io.martins.valhalla.exception.AlreadyExistsException;
import io.martins.valhalla.exception.handler.RestResponse;
import io.martins.valhalla.service.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/pessoas")
@RequiredArgsConstructor
@Tag(name = "Pessoas", description = "Conjunto de endpoints para gerenciar os dados relacionados a pessoa.")
public class PessoaController extends BaseController {

  private final PessoaService pessoaService;

  private final PessoaMapper pessoaMapper;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation( //
      summary = "Cria uma nova pessoa.", //
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
          content = @Content( //
              mediaType = "application/json", //
              schema = @Schema(implementation = PessoaVO.class), //
              examples = { //
                  @ExampleObject( //
                      name = "Exemplo de criação de pessoa", //
                      summary = "Básico", //
                      value = "{ \"cpf\": \"89084980875\", \"nome\": \"FULANO\", \"enderecos\": [{ \"cep\": \"22041012\" }] }" //
                  ) //
              } //
          ) //
      ), //
      responses = {
          @ApiResponse(responseCode = "201", description = "Pessoa criada com sucesso!", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PessoaDTO.class))), //
          @ApiResponse(responseCode = "400", description = "Um ou mais campos são inválidos!", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestResponse.class))) //
      }
  )
  public PessoaDTO create(@Parameter(description = "Informações da nova pessoa", required = true) @Valid @RequestBody final PessoaVO data) throws AlreadyExistsException, InterruptedException {
    final Pessoa pessoa = pessoaService.create(pessoaMapper.toEntity(data));

    return pessoaMapper.toDTO(pessoa);
  }

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(value = ID_PATH_VARIABLE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation( //
      summary = "Atualiza uma pessoa existente.", //
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
          content = @Content( //
              mediaType = "application/json", //
              schema = @Schema(implementation = PessoaVO.class), //
              examples = { //
                  @ExampleObject( //
                      name = "Exemplo de atualização de pessoa", //
                      summary = "Básico", //
                      value = "{ \"cpf\": \"89084980875\", \"nome\": \"CICLANO\", \"enderecos\": [{ \"cep\": \"22431050\" }] }" //
                  )
              }
          )
      ),
      responses = { //
          @ApiResponse(responseCode = "200", description = "Pessoa atualizada com sucesso!", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PessoaDTO.class))), //
          @ApiResponse(responseCode = "400", description = "Um ou mais campos são inválidos!", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestResponse.class))), //
          @ApiResponse(responseCode = "404", description = "Pessoa não encontrada!", content = @Content) //
      }
  )
  public PessoaDTO update( //
      @Parameter(description = "Identificador único da pessoa", required = true) @PathVariable("id") final Long id, //
      @Parameter(description = "Informações atualizadas da pessoa", required = true) @Valid @RequestBody final PessoaVO data) throws AlreadyExistsException {
    final Pessoa pessoa = pessoaService.update(pessoaMapper.toEntity(data, id));

    return pessoaMapper.toDTO(pessoa);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping(value = ID_PATH_VARIABLE)
  @Operation( //
      summary = "Exclui uma pessoa existente.", //
      responses = { //
          @ApiResponse(responseCode = "204", description = "Pessoa excluída com sucesso!", content = @Content), //
          @ApiResponse(responseCode = "404", description = "Pessoa não encontrada!", content = @Content) //
      }
  )
  public void delete(@Parameter(description = "Identificador único da pessoa", required = true) @PathVariable("id") final Long id) {
    pessoaService.delete(id);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PostMapping(value = "/{id}/enable")
  @Operation( //
      summary = "Ativa uma pessoa existente.", //
      responses = { //
          @ApiResponse(responseCode = "204", description = "Pessoa ativada com sucesso!", content = @Content), //
          @ApiResponse(responseCode = "404", description = "Pessoa não encontrada!", content = @Content) //
      }
  )
  public void enable(@Parameter(description = "Identificador único da pessoa", required = true) @PathVariable("id") final Long id) {
    pessoaService.enable(id);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PostMapping(value = "/{id}/disable")
  @Operation(
      summary = "Inativa uma pessoa existente.",
      responses = { //
          @ApiResponse(responseCode = "204", description = "Pessoa inativada com sucesso!", content = @Content), //
          @ApiResponse(responseCode = "404", description = "Pessoa não encontrada!", content = @Content) //
      }
  )
  public void disable(@Parameter(description = "Identificador único da pessoa", required = true) @PathVariable("id") final Long id) {
    pessoaService.disable(id);
  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = ID_PATH_VARIABLE, produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation( //
      summary = "Recupera uma pessoa existente.", //
      responses = { //
          @ApiResponse(responseCode = "200", description = "Pessoa recuperada com sucesso!", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PessoaDTO.class))), //
          @ApiResponse(responseCode = "404", description = "Pessoa não encontrada!", content = @Content) //
      }
  )
  public PessoaDTO findById(@Parameter(description = "Identificador único da pessoa", required = true) @PathVariable("id") final Long id) {
    final Pessoa pessoa = pessoaService.findById(id);

    return pessoaMapper.toDTO(pessoa);
  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation( //
      summary = "Recupera uma lista paginada de pessoas com base no filtro fornecido.", //
      responses = { //
          @ApiResponse(responseCode = "200", description = "Pessoas recuperadas com sucesso!", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PessoaDTO.class))) //
      }
  )
  public Page<PessoaDTO> findByFiltro( //
      @Parameter(description = "Filtro aplicado na pesquisa das pessoas", required = true) final FiltroPessoaVO filtro, //
      @Parameter(description = "Página a ser retornada (0..N)", required = true) @RequestParam final int page, //
      @Parameter(description = "Número de pessoas por página", required = true) @RequestParam final int size) {
    final Page<Pessoa> pessoaPage = pessoaService.findByFiltro(filtro, PageRequest.of(page, size));

    return pessoaPage.map(pessoaMapper::toDTO);
  }

}
