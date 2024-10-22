package io.martins.valhalla;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

// ===================================
// CLASSE PRINCIPAL
// ===================================
@SpringBootApplication
public class PaymentProcessingApplication {

  public static void main(String[] args) {
    SpringApplication.run(PaymentProcessingApplication.class, args);
  }
}

// ===================================
// ENTIDADES DE DOMÍNIO
// ===================================
@Data
@AllArgsConstructor
@NoArgsConstructor
class PaymentResult {
  private String status;
  private double remainingBalance;
  private double feeApplied;
  private String message;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class PaymentContext {
  private String accountId;
  private String paymentType;
  private double amount;
  private double balance;
  private double fee;
  private String transactionId;
  private boolean success;
}

// ===================================
// INTERFACE COMMAND
// ===================================
interface PaymentCommand {
  CompletableFuture<PaymentResult> execute(PaymentContext context);
}

// ===================================
// PROCESSADOR DE COMANDOS
// ===================================
class PaymentProcessor {

  private final List<PaymentCommand> commands;

  public PaymentProcessor(List<PaymentCommand> commands) {
    this.commands = commands;
  }

  public CompletableFuture<PaymentResult> process(PaymentContext context) {
    CompletableFuture<PaymentResult> result = CompletableFuture.completedFuture(null);

    for (PaymentCommand command : commands) {
      result = result.thenCompose(r -> command.execute(context));
    }

    return result;
  }
}

// ===================================
// EXECUTOR DE SERVIÇOS ASSÍNCRONOS
// ===================================
@Slf4j
class AsyncServiceExecutor {

  // Método genérico para executar uma tarefa com tratamento de exceção e simulação de erro
  public <T> CompletableFuture<T> executeAsyncWithErrorSimulation(Supplier<T> task, String taskName) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        log.info("Executando tarefa: {}", taskName);
        simulateProcessingTime();
        simulateRandomError(taskName);
        return task.get();
      } catch (Exception e) {
        log.error("Erro durante a execução de {}: {}", taskName, e.getMessage());
        throw new RuntimeException(e);
      }
    });
  }

  // Simula tempo de processamento
  private void simulateProcessingTime() throws InterruptedException {
    TimeUnit.SECONDS.sleep(1);
  }

  // Simula erro aleatório
  private void simulateRandomError(String taskName) {
    if (Math.random() < 0.2) { // 20% de chance de erro
      throw new RuntimeException("Erro aleatório na tarefa: " + taskName);
    }
  }
}

// ===================================
// IMPLEMENTAÇÕES DE COMANDOS
// ===================================
@Slf4j
class CheckBalanceCommand implements PaymentCommand {

  private final AsyncServiceExecutor executor = new AsyncServiceExecutor();

  @Override
  public CompletableFuture<PaymentResult> execute(PaymentContext context) {
    return executor.executeAsyncWithErrorSimulation(() -> {
      log.info("Verificando saldo para a conta: {}", context.getAccountId());
      double balance = 1000.00;  // Exemplo de saldo
      context.setBalance(balance);

      if (balance < 500) {
        context.setSuccess(false);
        return new PaymentResult("FAILURE", balance, 0.0, "Saldo insuficiente!");
      }

      context.setSuccess(true);
      return null;
    }, "Verificação de Saldo");
  }
}

@Slf4j
class CalculateFeeCommand implements PaymentCommand {

  private final AsyncServiceExecutor executor = new AsyncServiceExecutor();

  @Override
  public CompletableFuture<PaymentResult> execute(PaymentContext context) {
    if (!context.isSuccess()) {
      return CompletableFuture.completedFuture(new PaymentResult("FAILURE", context.getBalance(), 0.0, "Saldo insuficiente!"));
    }

    return executor.executeAsyncWithErrorSimulation(() -> {
      log.info("Calculando taxa para o tipo de pagamento: {}", context.getPaymentType());
      double fee = 25.00;
      context.setFee(fee);
      return null;
    }, "Cálculo de Taxas");
  }
}

@Slf4j
class ProcessPaymentCommand implements PaymentCommand {

  private final AsyncServiceExecutor executor = new AsyncServiceExecutor();

  @Override
  public CompletableFuture<PaymentResult> execute(PaymentContext context) {
    if (!context.isSuccess()) {
      return CompletableFuture.completedFuture(new PaymentResult("FAILURE", context.getBalance(), 0.0, "Erro na verificação de saldo ou taxa."));
    }

    return executor.executeAsyncWithErrorSimulation(() -> {
      log.info("Processando pagamento para a conta: {}", context.getAccountId());
      String transactionId = "TX123456789";
      context.setTransactionId(transactionId);
      context.setSuccess(true);

      return new PaymentResult(
          "SUCCESS", context.getBalance() - context.getFee(), context.getFee(),
          "Pagamento processado com sucesso. Transação ID: " + transactionId
      );
    }, "Processamento de Pagamento");
  }
}

// ===================================
// SERVIÇO DE PAGAMENTO
// ===================================
@Service
@RequiredArgsConstructor
class PaymentService {

  public CompletableFuture<PaymentResult> processFullPayment(String accountId, String paymentType, double amount) {
    PaymentContext context = new PaymentContext();
    context.setAccountId(accountId);
    context.setPaymentType(paymentType);
    context.setAmount(amount);

    PaymentProcessor processor = new PaymentProcessor(Arrays.asList(
        new CheckBalanceCommand(),
        new CalculateFeeCommand(),
        new ProcessPaymentCommand()
    ));

    return processor.process(context).exceptionally(ex -> new PaymentResult("FAILURE", 0, 0, "Erro no processamento: " + ex.getMessage()));
  }
}

// ===================================
// CONTROLADOR REST
// ===================================
@RestController
@RequestMapping(value = "/v1/payment")
@RequiredArgsConstructor
@Slf4j
class PaymentController {

  private final PaymentService paymentService;

  @GetMapping("/process")
  public CompletableFuture<PaymentResult> processPayment(
      @RequestParam String accountId,
      @RequestParam String paymentType,
      @RequestParam double amount
  ) {
    log.info("Recebida solicitação de pagamento. Conta: {}, Tipo: {}, Quantia: {}", accountId, paymentType, amount);
    return paymentService.processFullPayment(accountId, paymentType, amount);
  }
}
