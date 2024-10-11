package io.martins.valhalla.service;


import io.martins.valhalla.client.ViaCepClient;
import io.martins.valhalla.constant.MessageConstants;
import io.martins.valhalla.domain.dto.nested.EnderecoDTO;
import io.martins.valhalla.domain.entity.Endereco;
import io.martins.valhalla.domain.entity.Pessoa;
import io.martins.valhalla.domain.enumeration.StatusEnum;
import io.martins.valhalla.domain.mapper.PessoaMapper;
import io.martins.valhalla.domain.vo.FiltroPessoaVO;
import io.martins.valhalla.exception.AlreadyExistsException;
import io.martins.valhalla.exception.RecordNotFoundException;
import io.martins.valhalla.repository.PessoaRepository;
import io.martins.valhalla.repository.specification.PessoaSpecifications;
import io.martins.valhalla.util.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PessoaService {

  private final PessoaRepository pessoaRepository;

  private final PessoaMapper pessoaMapper;

  private final ViaCepClient viacepClient;

  public Pessoa create(final Pessoa pessoa) throws AlreadyExistsException, InterruptedException {
    pessoa.setEnderecos(pessoa.getEnderecos().stream() //
        .map(e -> {
          try {
            return findEndereco(e.getCep());
          } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
          }
        }) //
        .toList());

    pessoa.getEnderecos().forEach(e -> e.setPessoa(pessoa));

    try {
      //return pessoaRepository.save(pessoa);

      Thread.sleep(1000); // simulando uma tarefa assíncrona complexa

      return pessoa;

    } catch (DataIntegrityViolationException e) {
      throw new AlreadyExistsException(MessageConstants.CPF_JA_EXISTENTE);
    }
  }

  public Pessoa update(final Pessoa pessoa) throws AlreadyExistsException {
    final Pessoa persisted = pessoaRepository.findById(pessoa.getId()).orElseThrow(RecordNotFoundException::new);

    pessoa.setEnderecos(pessoa.getEnderecos().stream() //
        .map(e -> {
          final Endereco newz;

          // temp
          try {
            newz = findEndereco(e.getCep());
          } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
          }
          newz.setId(persisted.getId());

          return newz;
        }) //
        .toList());

    try {
      pessoaMapper.merge(persisted, pessoa);

      return pessoaRepository.save(persisted);

    } catch (DataIntegrityViolationException e) {
      throw new AlreadyExistsException(MessageConstants.CPF_JA_EXISTENTE);
    }
  }

  @Transactional
  public void delete(final Long id) {
    final int updatedRows = pessoaRepository.updateStatus(id, StatusEnum.EXCLUIDO);

    if (updatedRows == 0) {
      throw new RecordNotFoundException();
    }
  }

  @Transactional
  public void enable(final Long id) {
    final int updatedRows = pessoaRepository.updateStatus(id, StatusEnum.ATIVO);

    if (updatedRows == 0) {
      throw new RecordNotFoundException();
    }
  }

  @Transactional
  public void disable(final Long id) {
    final int updatedRows = pessoaRepository.updateStatus(id, StatusEnum.INATIVO);

    if (updatedRows == 0) {
      throw new RecordNotFoundException();
    }
  }

  public Pessoa findById(final Long id) throws RecordNotFoundException {
    return pessoaRepository.findById(id).orElseThrow(RecordNotFoundException::new);
  }

  public Page<Pessoa> findByFiltro(final FiltroPessoaVO filtro, final Pageable pageable) {
    final List<Specification<Pessoa>> specifications = new ArrayList<>();

    if (StringUtils.isNotBlank(filtro.cpf())) {
      specifications.add(PessoaSpecifications.cpfEquals(filtro.cpf()));
    }

    if (StringUtils.isNotBlank(filtro.nome())) {
      specifications.add(PessoaSpecifications.nomeLike(filtro.nome()));
    }

    if (filtro.status() != null) {
      specifications.add(PessoaSpecifications.statusEquals(filtro.status()));
    }

    return pessoaRepository.findAll(Specification.allOf(specifications), pageable);
  }

  private Endereco findEndereco(final String cep) throws InterruptedException {
    //final EnderecoDTO endereco = viacepClient.findEnderecoByCep(cep) //
        //.orElseThrow(() -> new RecordNotFoundException(MessageConstants.ENDERECO_NAO_ENCONTRADO_PARA_CEP_INFORMADO));

    Thread.sleep(1000); // simulando uma tarefa assíncrona complexa

    final var endereco = simulate(); // simulando a resposta do servico

    final Endereco entity = new Endereco();
    entity.setCep(StringUtils.removeNonDigits(cep));
    entity.setLogradouro(endereco.getLogradouro());
    entity.setComplemento(endereco.getComplemento());
    entity.setBairro(endereco.getBairro());
    entity.setLocalidade(endereco.getLocalidade());
    entity.setUf(endereco.getUf());
    entity.setIbge(endereco.getIbge());

    return entity;
  }

  private static Endereco simulate() {
    final var endereco = new Endereco();
    endereco.setCep("22041012");
    endereco.setLogradouro("Rua Santa Clara");
    endereco.setComplemento("de 2 ao fim - lado par");
    endereco.setBairro("Copacabana");
    endereco.setLocalidade("Rio de Janeiro");
    endereco.setUf("RJ");
    endereco.setIbge(3304557);
    endereco.setDataInclusao(LocalDateTime.parse("2024-10-11T01:32:43.317953"));
    endereco.setDataAtualizacao(LocalDateTime.parse("2024-10-11T01:32:43.317953"));

    return endereco;
  }

}
