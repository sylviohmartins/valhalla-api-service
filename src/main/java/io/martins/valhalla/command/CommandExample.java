//package io.martins.valhalla.command;
//
//import io.martins.valhalla.command.nested.BaseCommand;
//import io.martins.valhalla.command.nested.Command;
//import io.martins.valhalla.command.nested.Context;
//import io.martins.valhalla.command.nested.Processor;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@SpringBootApplication
//public class OrderSimulation {
//
//  public static void main(String[] args) {
//    SpringApplication.run(OrderSimulation.class, args);
//  }
//
//  @RestController
//  @RequestMapping("/order")
//  public static class OrderController {
//
//    private final OrderCommand orderCommand;
//
//    public OrderController(OrderCommand orderCommand) {
//      this.orderCommand = orderCommand;
//    }
//
//    @PostMapping
//    public String placeOrder() {
//      Context context = new Context("Pedido de bolo - Execução com múltiplos processos");
//      orderCommand.execute(context);
//      return "Pedido processado com sucesso!";
//    }
//  }
//
//  // Interface para o comando do pedido
//  public interface OrderCommand extends Command {
//  }
//
//  // Implementação do comando de pedido usando BaseCommand
//  @Component
//  @RequiredArgsConstructor
//  public static class OrderCommandImpl extends BaseCommand implements OrderCommand {
//
//    private final PrepararBolo prepararBolo;
//    private final VerificarQualidade verificarQualidade;
//    private final Decorar decorar;
//    private final Embalar embalar;
//    private final VendaAcompanhamentos vendaAcompanhamentos;
//    private final Entregar entregar;
//
//    // Configuração inicial de processadores e callback
//    @PostConstruct
//    public void init() {
//      addProcessors(prepararBolo, verificarQualidade, decorar, embalar, vendaAcompanhamentos, entregar);
//      super.setCallback(this::onCompletion);
//    }
//
//    private void onCompletion() {
//      log("Processamento de pedido concluído com sucesso.");
//    }
//
//    @Override
//    public void execute(final Context context) {
//      super.run(context);
//    }
//  }
//
//  @Component
//  @RequiredArgsConstructor
//  public static class PrepararBolo implements Processor {
//    @Override
//    public void doProcess(Context context) {
//      log("[1] Preparando a camada do bolo...");
//    }
//  }
//
//  @Component
//  @RequiredArgsConstructor
//  public static class VerificarQualidade implements Processor {
//    @Override
//    public void doProcess(Context context) {
//      log("[2] Verificando qualidade da camada do bolo...");
//      if (Math.random() > 0.8) {
//        throw new RuntimeException("Falha na qualidade da camada do bolo.");
//      }
//    }
//
//    @Override
//    public List<Class<? extends Processor>> getDependencies() {
//      return List.of(PrepararBolo.class);
//    }
//  }
//
//  @Component
//  @RequiredArgsConstructor
//  public static class Decorar implements Processor {
//    @Override
//    public void doProcess(Context context) {
//      log("[3] Montando e decorando o bolo...");
//    }
//
//    @Override
//    public List<Class<? extends Processor>> getDependencies() {
//      return List.of(VerificarQualidade.class);
//    }
//  }
//
//  @Component
//  @RequiredArgsConstructor
//  public static class Embalar implements Processor {
//    @Override
//    public void doProcess(Context context) {
//      log("[4] Embalando o bolo...");
//    }
//
//    @Override
//    public List<Class<? extends Processor>> getDependencies() {
//      return List.of(Decorar.class);
//    }
//  }
//
//  @Component
//  @RequiredArgsConstructor
//  public static class VendaAcompanhamentos implements Processor {
//    @Override
//    public void doProcess(Context context) {
//      log("[5] Vendendo acompanhamentos...");
//    }
//  }
//
//  @Component
//  @RequiredArgsConstructor
//  public static class Entregar implements Processor {
//    @Override
//    public void doProcess(Context context) {
//      log("[6] Entregando o bolo...");
//    }
//
//    @Override
//    public List<Class<? extends Processor>> getDependencies() {
//      return List.of(Embalar.class, VendaAcompanhamentos.class);
//    }
//  }
//
//  // Método de log para registrar o tempo de execução
//  private static void log(String message) {
//    System.out.println(LocalDateTime.now() + " - " + message);
//  }
//}
