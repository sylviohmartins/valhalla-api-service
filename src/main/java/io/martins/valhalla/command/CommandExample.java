package io.martins.valhalla.command;

import io.martins.valhalla.command.nested.BaseCommand;
import io.martins.valhalla.command.nested.Context;
import io.martins.valhalla.command.nested.Processor;

import java.util.List;

public class CommandExample {

  public static void main(String[] args) {
    Context context = new Context("Contexto de venda de bolo");

    List<Processor> processors = List.of(
        new PreparoBolo(),
        new EmbalagemBolo(),
        new VendaBolo(),
        new EntregaBolo(),
        new VendaAcompanhamento()  // Novo processo
    );

    BaseCommand command = new BaseCommand(processors) {};
    try {
      command.run(context);
    } catch (Exception e) {
      System.out.println("Erro na execução dos comandos: " + e.getMessage());
      e.printStackTrace();
    }
  }

  // Implementações dos Processadores

  static class PreparoBolo implements Processor {

    @Override
    public void doProcess(Context context) {
      System.out.println("Preparando o bolo...");
    }

  }

  static class EmbalagemBolo implements Processor {

    @Override
    public void doProcess(Context context) {
      System.out.println("Embalando o bolo...");
    }

  }

  static class VendaBolo implements Processor {

    @Override
    public void doProcess(Context context) {
      System.out.println("Vendendo o bolo...");
    }

  }

  static class EntregaBolo implements Processor {

    @Override
    public void doProcess(Context context) {
      System.out.println("Entregando o bolo...");
    }

  }

  // Um processo que pode ser desativado
  static class VendaAcompanhamento implements Processor {

    @Override
    public void doProcess(Context context) {
      System.out.println("Vendendo acompanhamento...");
    }

  }
}
