package io.martins.valhalla.command.nested;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseCommand {

  private final List<Processor> processors;

  private final Map<Class<? extends Processor>, CompletableFuture<Void>> dependencyFutures = new ConcurrentHashMap<>();

  public BaseCommand(List<Processor> processors) {
    this.processors = processors;
  }

  public void run(final Context context) {
    try (ConcurrentTaskScope<Void> taskScope = new ConcurrentTaskScope<>()) {
      processors.forEach(processor -> {
        CompletableFuture<Void> processorFuture = submitProcessorWithDependencies(taskScope, processor, context);
        dependencyFutures.put(processor.getClass(), processorFuture);  // Mapeia o futuro do processador
      });

      // Aguarda a conclusão de todas as tarefas submetidas
      taskScope.awaitCompletion();

      // Lança exceção em caso de falha em qualquer processador
      taskScope.throwIfFailed(() -> new RuntimeException("Falha na execução de um ou mais processadores."));

    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  private CompletableFuture<Void> submitProcessorWithDependencies(ConcurrentTaskScope<Void> taskScope, Processor processor, Context context) {
    List<Class<? extends Processor>> dependencies = processor.getDependencies();

    if (dependencies.isEmpty()) {
      // Se não houver dependências, submete o processador diretamente
      return taskScope.submitTask(() -> executeProcessor(processor, context));
    }

    // Aguarda a conclusão de todas as dependências antes de executar o processador
    return taskScope.submitTask(() -> {
      awaitDependencies(dependencies); // Aguarda as dependências serem resolvidas

      return executeProcessor(processor, context); // Executa o processador
    });
  }

  private void awaitDependencies(List<Class<? extends Processor>> dependencies) {
    CompletableFuture<Void> dependenciesFuture = CompletableFuture.allOf(
        dependencies.stream()
            .map(this::getDependencyFuture)
            .toArray(CompletableFuture[]::new)
    );
    dependenciesFuture.join(); // Bloqueia até que todas as dependências sejam resolvidas
  }

  private CompletableFuture<Void> getDependencyFuture(Class<? extends Processor> dependency) {
    IllegalStateException exception = new IllegalStateException("Dependência não encontrada: " + dependency.getSimpleName());

    return dependencyFutures.getOrDefault(dependency, CompletableFuture.failedFuture(exception));
  }

  private Void executeProcessor(Processor processor, Context context) {
    if (processor.supports(context)) {
      processor.doProcess(context);

    }

    return null;
  }

}
