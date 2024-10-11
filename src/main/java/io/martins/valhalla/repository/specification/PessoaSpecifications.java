package io.martins.valhalla.repository.specification;

import io.martins.valhalla.domain.entity.Pessoa;
import io.martins.valhalla.domain.entity.Pessoa_;
import io.martins.valhalla.domain.enumeration.StatusEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PessoaSpecifications {

	public static Specification<Pessoa> cpfEquals(final String cpf) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Pessoa_.cpf), cpf);
	}

	public static Specification<Pessoa> nomeLike(final String nome) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(Pessoa_.nome)), "%" + nome.toLowerCase() + "%");
	}

	public static Specification<Pessoa> statusEquals(final StatusEnum status) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Pessoa_.status), status);
	}

}
