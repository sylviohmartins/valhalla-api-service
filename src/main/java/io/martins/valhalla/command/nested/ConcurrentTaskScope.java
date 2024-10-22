package io.martins.valhalla.command.nested;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class ConcurrentTaskScope<T> implements AutoCloseable {

  // Lista de tarefas concorrentes
  private final List<CompletableFuture<T>> tasks = new ArrayList<>();

  // Referência atômica para capturar a primeira falha
  private final AtomicReference<Throwable> firstFailure = new AtomicReference<>();

  public CompletableFuture<T> submitTask(Callable<T> task) {
    CompletableFuture<T> futureTask = CompletableFuture.supplyAsync(() -> {
      try {
        // Verifica e executa a tarefa
        return task.call();

      } catch (Exception e) {
        recordFailure(e);  // Registra a falha
        throw new CompletionException(e);
      }
    });

    tasks.add(futureTask);  // Adiciona a tarefa na lista
    return futureTask;
  }

  public void awaitCompletion() {
    for (CompletableFuture<T> task : tasks) {
      try {
        task.join();  // Aguarda cada tarefa individualmente

        if (firstFailure.get() != null) {
          cancelPendingTasks();  // Cancela as demais tarefas imediatamente em caso de falha
          throw new RuntimeException(firstFailure.get());  // Lança a exceção de falha
        }

      } catch (CompletionException e) {
        recordFailure(e.getCause());  // Registra a falha e propaga
        cancelPendingTasks();  // Cancela todas as tarefas pendentes
        throw new RuntimeException("Erro durante a execução das tarefas", e.getCause());
      }
    }
  }

  public void throwIfFailed(Supplier<? extends Throwable> exceptionSupplier) throws Throwable {
    if (firstFailure.get() != null) {
      cancelPendingTasks();  // Cancela todas as tarefas pendentes
      throw exceptionSupplier.get();  // Lança a exceção fornecida
    }
  }

  @Override
  public void close() {
    cancelPendingTasks();  // Cancela todas as tarefas ao encerrar o escopo
  }

  private void recordFailure(Throwable failure) {
    firstFailure.compareAndSet(null, failure);  // Registra apenas a primeira falha
  }

  private void cancelPendingTasks() {
    tasks.forEach(task -> task.cancel(true));  // Cancela todas as tarefas pendentes
  }

}
