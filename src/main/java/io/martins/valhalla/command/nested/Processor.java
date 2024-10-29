package io.martins.valhalla.command.nested;


import java.util.List;

/**
 * Interface representando um processador que pode ser executado em um contexto de comando. Processadores podem declarar dependências de outros processadores e especificar se podem ser executados em paralelo.
 */
public interface Processor {

  /**
   * Executa a lógica do processador dentro do contexto fornecido.
   *
   * @param context o contexto de execução.
   *                <p>
   *                Exemplo de uso:
   *                <pre>{@code
   *                                                             Processor processA = new ProcessoA();
   *                                                             processA.doProcess(new Context("Dados do Processo A"));
   *                                                             }</pre>
   */
  void doProcess(Context context) throws Exception;

  /**
   * Verifica se o processador suporta a execução no contexto fornecido.
   *
   * @param context o contexto de execução.
   * @return true se o processador suporta o contexto, false caso contrário.
   * <p>
   * Exemplo de uso:
   * <pre>{@code
   * Processor processA = new ProcessoA();
   * boolean isSupported = processA.supports(new Context("Contexto qualquer"));
   * }</pre>
   */
  default boolean supports(Context context) {
    return true;
  }

  /**
   * Retorna a lista de classes de processadores dos quais este processador depende.
   *
   * @return lista de classes de processadores dependentes.
   * <p>
   * Exemplo de uso:
   * <pre>{@code
   * Processor processB = new ProcessoB();
   * List<Class<? extends Processor>> deps = processB.getDependencies();
   * }</pre>
   */
  default List<Class<? extends Processor>> getDependencies() {
    return List.of();
  }

}