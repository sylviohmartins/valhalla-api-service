package io.martins.valhalla.command.nested;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

public abstract class BaseCommand {

  private final List<Processor> processors = new ArrayList<>();

  private final Map<Class<? extends Processor>, CompletableFuture<Void>> dependencyFutures = new ConcurrentHashMap<>();

  private ErrorHandler errorHandler;

  public void addProcessors(Processor... processors) {
    this.processors.addAll(Arrays.asList(processors));
  }

  public void errorHandler(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  public void run(final Context context) {
    try (TaskExecutionScope<Void> taskScope = new TaskExecutionScope<>()) {
      processors.forEach(processor -> dependencyFutures.put(processor.getClass(), submitProcessorWithDependencies(taskScope, processor, context)));

      taskScope.waitForCompletion();
      taskScope.throwOnFailure(() -> new RuntimeException("Falha na execução de um ou mais processadores."));

    } catch (Throwable e) {
      handleExecutionError(context, e);
    }
  }

  private CompletableFuture<Void> submitProcessorWithDependencies(TaskExecutionScope<Void> taskScope, Processor processor, Context context) {
    if (processor.getDependencies().isEmpty()) {
      return taskScope.submitTask(() -> executeProcessor(processor, context));
    }

    return taskScope.submitTask(() -> {
      awaitProcessorDependencies(processor.getDependencies());

      return executeProcessor(processor, context);
    });
  }

  private void awaitProcessorDependencies(List<Class<? extends Processor>> dependencies) {
    CompletableFuture<Void> allDependencies = CompletableFuture.allOf(
        dependencies.stream()
            .map(this::getDependencyFuture)
            .toArray(CompletableFuture[]::new)
    );

    allDependencies.join();
  }

  private CompletableFuture<Void> getDependencyFuture(Class<? extends Processor> dependency) {
    final CompletableFuture<Void> failedFuture = CompletableFuture.failedFuture(new IllegalStateException("Dependência não encontrada: " + dependency.getSimpleName()));

    return dependencyFutures.getOrDefault(dependency, failedFuture);
  }

  private Void executeProcessor(Processor processor, Context context) throws Exception {
    if (processor.supports(context)) {
      processor.doProcess(context);
    }

    return null;
  }

  private void handleExecutionError(Context context, Throwable e) {
    if (errorHandler != null) {
      errorHandler.handleError(context, e);

    } else {
      e.printStackTrace();
    }
  }

}
