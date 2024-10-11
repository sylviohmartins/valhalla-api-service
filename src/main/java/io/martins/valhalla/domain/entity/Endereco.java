package io.martins.valhalla.domain.entity;

import io.martins.valhalla.constant.SchemaConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Setter
@Getter
@Entity
@Table(schema = SchemaConstants.PRINCIPAL, name = "tb_pessoa_endereco")
public class Endereco implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_pessoa", referencedColumnName = "id")
  private Pessoa pessoa;

  @Column(name = "cep")
  private String cep;

  @Column(name = "logradouro")
  private String logradouro;

  @Column(name = "complemento")
  private String complemento;

  @Column(name = "bairro")
  private String bairro;

  @Column(name = "localidade")
  private String localidade;

  @Column(name = "uf")
  private String uf;

  @Column(name = "ibge")
  private Integer ibge;

  @CreationTimestamp
  @Column(name = "data_inclusao")
  private LocalDateTime dataInclusao;

  @UpdateTimestamp
  @Column(name = "data_atualizacao")
  private LocalDateTime dataAtualizacao;

}
