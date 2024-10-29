package io.martins.valhalla.command.nested;

import java.io.Serial;
import java.util.HashMap;
import java.util.Optional;


/**
 * Representa um contexto que armazena dados e resultados para execução de comandos.
 */
public class Context extends HashMap<String, Object> {

  @Serial
  private static final long serialVersionUID = 1L;

  private static final String DATA = "data";

  private static final String RESULT = "result";

  /**
   * Construtor que inicializa o contexto com os dados fornecidos.
   *
   * @param data os dados iniciais do contexto.
   */
  public Context(final Object data) {
    setProperty(DATA, data);
  }

  /**
   * Retorna os dados armazenados no contexto.
   *
   * @param <T> o tipo esperado dos dados.
   * @param clazz a classe dos dados esperados.
   * @return os dados armazenados, ou null se não forem encontrados.
   */
  public <T> T getData(final Class<T> clazz) {
    return getProperty(DATA, clazz).orElse(null);
  }

  /**
   * Define novos dados no contexto.
   *
   * @param data os dados a serem armazenados.
   */
  public void setData(final Object data) {
    setProperty(DATA, data);
  }

  /**
   * Obtém o resultado armazenado no contexto.
   *
   * @param <T> o tipo esperado do resultado.
   * @param clazz a classe do resultado esperado.
   * @return o resultado armazenado, ou null se não for encontrado.
   */
  public <T> T getResult(final Class<T> clazz) {
    return getProperty(RESULT, clazz).orElse(null);
  }

  /**
   * Define o resultado no contexto.
   *
   * @param result o resultado a ser armazenado.
   */
  public void setResult(final Object result) {
    setProperty(RESULT, result);
  }

  public <T> void setProperty(final String key, final Object value) {
    this.put(key, value);
  }

  public <T> Optional<T> getProperty(final String key, final Class<T> clazz) {
    return Optional.ofNullable(get(key)).filter(clazz::isInstance).map(clazz::cast);
  }

}