package io.martins.valhalla.domain.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import io.martins.valhalla.domain.dto.PessoaDTO;
import io.martins.valhalla.domain.entity.Endereco;
import io.martins.valhalla.domain.entity.Pessoa;
import io.martins.valhalla.domain.vo.PessoaVO;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = SPRING)
public interface PessoaMapper {

  @Mapping(target = "iid", ignore = true)
  PessoaDTO toDTO(Pessoa source);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "versao", ignore = true)
  @Mapping(target = "dataAtualizacao", ignore = true)
  @Mapping(target = "dataCriacao", ignore = true)
  Pessoa toEntity(PessoaVO source);

  @Mapping(source = "id", target = "id")
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "versao", ignore = true)
  @Mapping(target = "dataAtualizacao", ignore = true)
  @Mapping(target = "dataCriacao", ignore = true)
  Pessoa toEntity(PessoaVO source, Long id);

  @Mapping(source = "modified.id", target = "id", ignore = true)
  @Mapping(source = "modified.versao", target = "versao", ignore = true)
  @Mapping(source = "modified.dataCriacao", target = "dataCriacao", ignore = true)
  @Mapping(source = "modified.dataAtualizacao", target = "dataAtualizacao", ignore = true)
  void merge(@MappingTarget Pessoa persisted, Pessoa modified);

  @Mapping(source = "modified.pessoa", target = "pessoa", ignore = true)
  @Mapping(source = "modified.dataInclusao", target = "dataInclusao", ignore = true)
  @Mapping(source = "modified.dataAtualizacao", target = "dataAtualizacao", ignore = true)
  void mergeEndereco(@MappingTarget Endereco persisted, Endereco modified);

  default void mergeEnderecoList(@MappingTarget final List<Endereco> persistedList, final List<Endereco> modifiedList) {
    final Map<Long, Endereco> persistedMap = persistedList.stream().collect(Collectors.toMap(Endereco::getId, e -> e));

    for (final Endereco modified : modifiedList) {
      if (persistedMap.get(modified.getId()) == null) {
        persistedList.add(modified); // inclusao

      } else {
        mergeEndereco(persistedMap.get(modified.getId()), modified); // atualizacao
      }
    }

    final Set<Long> modifiedIds = modifiedList.stream() //
        .map(Endereco::getId) //
        .filter(Objects::nonNull) //
        .collect(Collectors.toSet());

    persistedList.removeIf(persisted -> !modifiedIds.contains(persisted.getId())); // exclusao
  }

  @AfterMapping
  default void afterPessoaMapping(@MappingTarget final Pessoa persisted, final Pessoa modified) {
    persisted.getEnderecos().forEach(endereco -> endereco.setPessoa(persisted));
  }

}
