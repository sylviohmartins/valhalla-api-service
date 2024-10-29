package io.martins.valhalla.command.nested;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class TaskExecutionScope<T> implements AutoCloseable {

  private final List<CompletableFuture<T>> tasks = new ArrayList<>();

  private final AtomicReference<Throwable> firstError = new AtomicReference<>();

  public CompletableFuture<T> submitTask(Callable<T> task) {
    CompletableFuture<T> futureTask = CompletableFuture.supplyAsync(() -> executeTask(task));

    tasks.add(futureTask);

    return futureTask;
  }

  public void waitForCompletion() {
    tasks.forEach(this::awaitTaskCompletion);
  }

  public void throwOnFailure(Supplier<? extends Throwable> exceptionSupplier) throws Throwable {
    Throwable error = firstError.get();

    if (error != null) {
      cancelAllPendingTasks();

      throw exceptionSupplier.get();
    }
  }

  @Override
  public void close() {
    cancelAllPendingTasks();
  }

  private T executeTask(Callable<T> task) {
    try {
      return task.call();

    } catch (Exception e) {
      recordFirstError(e);

      throw new CompletionException(e);
    }
  }

  private void awaitTaskCompletion(CompletableFuture<T> task) {
    try {
      task.join();

      checkAndHandleFailure();

    } catch (CompletionException e) {
      recordFirstError(e.getCause());

      cancelAllPendingTasks();

      throw new RuntimeException("Erro durante a execução das tarefas", e.getCause());
    }
  }

  private void recordFirstError(Throwable error) {
    firstError.compareAndSet(null, error);
  }

  private void checkAndHandleFailure() {
    Throwable error = firstError.get();

    if (error != null) {
      cancelAllPendingTasks();

      throw new RuntimeException(error);
    }
  }

  private void cancelAllPendingTasks() {
    tasks.forEach(task -> task.cancel(true));
  }

}
