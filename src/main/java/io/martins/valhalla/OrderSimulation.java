package io.martins.valhalla;

import io.martins.valhalla.command.nested.BaseCommand;
import io.martins.valhalla.command.nested.Command;
import io.martins.valhalla.command.nested.Context;
import io.martins.valhalla.command.nested.ErrorHandler;
import io.martins.valhalla.command.nested.Processor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

@SpringBootApplication
public class OrderSimulation {

  public static void main(String[] args) {
    SpringApplication.run(OrderSimulation.class, args);
  }

  @RestController
  @RequestMapping("/order")
  @RequiredArgsConstructor
  public static class OrderController {

    private final OrderCommandImpl orderCommand;

    @PostMapping
    public String placeOrder() {
      Context context = new Context("Pedido de comida completo com várias dependências e validações");
      orderCommand.execute(context);
      return "Pedido processado com sucesso!";
    }
  }

  public interface OrderCommand extends Command {}

  @Component
  @RequiredArgsConstructor
  public static class OrderCommandImpl extends BaseCommand implements OrderCommand {

    private final PedidoUsuario pedidoUsuario;
    private final PrepararPedido prepararPedido;
    private final EmbalarPedido embalarPedido;
    private final VerificarQualidade verificarQualidade;
    private final DespacharPedido despacharPedido;
    private final RealizarEntrega realizarEntrega;
    private final PosEntrega posEntrega;
    private final OrderErrorHandler errorHandler;

    @PostConstruct
    public void init() {
      super.addProcessors(pedidoUsuario, prepararPedido, embalarPedido, verificarQualidade, despacharPedido, realizarEntrega, posEntrega);
      super.errorHandler(errorHandler);
    }

    @Override
    public void execute(final Context context) {
      super.run(context);
    }
  }

  @Component
  @RequiredArgsConstructor
  public static class OrderErrorHandler extends ErrorHandler {

    @Override
    public void handleError(final Context context, final Throwable e) {
      if (e instanceof TimeoutException) {
        log("Erro: Tarefa excedeu o tempo limite - Contexto: " + context);
      } else if (e instanceof RuntimeException) {
        log("Erro no processamento do pedido: " + e.getMessage() + " - Contexto: " + context);
      } else {
        log("Erro inesperado: " + e.getMessage() + " - Contexto: " + context);
      }
    }
  }

  // Processor para simular o início do pedido do usuário
  @Component
  @RequiredArgsConstructor
  public static class PedidoUsuario implements Processor {
    @Override
    public void doProcess(Context context) throws TimeoutException {
      runWithTimeout(() -> log("Iniciando pedido do usuário..."), 200);
    }
  }

  // Processor para simular a preparação do pedido com retry
  @Component
  @RequiredArgsConstructor
  public static class PrepararPedido implements Processor {
    @Override
    public void doProcess(Context context) throws TimeoutException {
      runWithTimeoutRetry(() -> {
        log("Preparando o pedido...");
        if (Math.random() > 0.8) throw new RuntimeException("Falha na preparação do pedido.");
      }, 500, 3, e -> e instanceof RuntimeException);
    }

  }

  // Processor para simular a embalagem do pedido
  @Component
  @RequiredArgsConstructor
  public static class EmbalarPedido implements Processor {
    @Override
    public void doProcess(Context context) throws TimeoutException {
      runWithTimeout(() -> log("Embalando o pedido..."), 300);
    }

  }

  // Processor para verificar a qualidade com retry
  @Component
  @RequiredArgsConstructor
  public static class VerificarQualidade implements Processor {
    @Override
    public void doProcess(Context context) throws TimeoutException {
      runWithTimeoutRetry(() -> {
        log("Verificando a qualidade do pedido...");
        if (Math.random() > 0.7) throw new RuntimeException("Falha na verificação de qualidade.");
      }, 400, 2, e -> e instanceof RuntimeException);
    }

    @Override
    public List<Class<? extends Processor>> getDependencies() {
      return List.of(EmbalarPedido.class);
    }
  }

  // Processor para simular o despacho do pedido
  @Component
  @RequiredArgsConstructor
  public static class DespacharPedido implements Processor {
    @Override
    public void doProcess(Context context) throws TimeoutException {
      runWithTimeout(() -> log("Despachando o pedido para entrega..."), 300);
    }
  }

  // Processor para simular a entrega com retry e potencial falha
  @Component
  @RequiredArgsConstructor
  public static class RealizarEntrega implements Processor {
    @Override
    public void doProcess(Context context) throws TimeoutException {
      runWithTimeoutRetry(() -> {
        log("Realizando a entrega ao cliente...");
        if (Math.random() > 0.75) throw new RuntimeException("Falha na entrega.");
      }, 600, 1, e -> e instanceof RuntimeException);
    }

  }

  // Processor para simular o feedback do pós-entrega
  @Component
  @RequiredArgsConstructor
  public static class PosEntrega implements Processor {
    @Override
    public void doProcess(Context context) throws TimeoutException {
      runWithTimeout(() -> log("Realizando pós-entrega e solicitando feedback..."), 300);
    }

  }

  // Utility para aplicar timeout em tarefas
  private static void runWithTimeout(Runnable task, long timeoutMillis) throws TimeoutException {
    CompletableFuture<Void> future = CompletableFuture.runAsync(task);
    try {
      future.get(timeoutMillis, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      future.cancel(true);
      throw new TimeoutException("Tempo limite excedido para a tarefa.");
    }
  }

  // Utility para aplicar retry com lógica de backoff exponencial
  private static void runWithTimeoutRetry(Runnable task, long timeoutMillis, int maxRetries, Predicate<Throwable> retryCondition) throws TimeoutException {
    AtomicInteger retryCount = new AtomicInteger(0);
    CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
      while (retryCount.get() <= maxRetries) {
        try {
          task.run();
          return null;
        } catch (Throwable e) {
          if (retryCondition.test(e) && retryCount.incrementAndGet() <= maxRetries) {
            log("Tentativa " + retryCount.get() + " falhou, aguardando antes de nova tentativa...");
            sleepExponentialBackoff(retryCount.get());
          } else {
            throw e;
          }
        }
      }
      return null;
    });
    try {
      future.get(timeoutMillis, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      future.cancel(true);
      throw new TimeoutException("Tempo limite excedido para a tarefa.");
    }
  }

  // Exponencial backoff para retries
  private static void sleepExponentialBackoff(int retryCount) {
    try {
      Thread.sleep((long) Math.pow(2, retryCount) * 100L);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private static void log(String message) {
    System.out.println(LocalDateTime.now() + " - " + message);
  }
}
