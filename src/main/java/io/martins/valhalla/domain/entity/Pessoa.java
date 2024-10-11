package io.martins.valhalla.domain.entity;

import io.martins.valhalla.constant.SchemaConstants;
import io.martins.valhalla.domain.converter.StatusConverter;
import io.martins.valhalla.domain.enumeration.StatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Setter
@Getter
@Entity
@Table(schema = SchemaConstants.PRINCIPAL, name = "tb_pessoa")
@NamedEntityGraph(name = "Pessoa.enderecos", attributeNodes = @NamedAttributeNode("enderecos"))
public class Pessoa implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(mappedBy = "pessoa", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private List<Endereco> enderecos;

  @Column(name = "cpf")
  private String cpf;

  @Column(name = "nome")
  private String nome;

  @Version
  @Column(name = "versao")
  private Integer versao;

  @Column(name = "id_status_pessoa")
  @Convert(converter = StatusConverter.class)
  private StatusEnum status = StatusEnum.ATIVO;

  @CreationTimestamp
  @Column(name = "data_criacao")
  private LocalDateTime dataCriacao;

  @UpdateTimestamp
  @Column(name = "data_atualizacao")
  private LocalDateTime dataAtualizacao;

}
