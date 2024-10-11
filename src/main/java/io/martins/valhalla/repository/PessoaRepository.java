package io.martins.valhalla.repository;

import io.martins.valhalla.domain.entity.Pessoa;
import io.martins.valhalla.domain.enumeration.StatusEnum;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

public interface PessoaRepository extends JpaRepository<Pessoa, Long>, JpaSpecificationExecutor<Pessoa> {

  @Modifying
  @Query("UPDATE Pessoa p SET p.status = :status WHERE p.id = :id")
  int updateStatus(@Param(value = "id") final Long id, @Param(value = "status") final StatusEnum status);

  @Override
  @EntityGraph(value = "Pessoa.enderecos")
  Optional<Pessoa> findById(final Long id);

  @Override
  @EntityGraph(value = "Pessoa.enderecos")
  Page<Pessoa> findAll(@Nullable final Specification<Pessoa> spec, final Pageable pageable);

}
