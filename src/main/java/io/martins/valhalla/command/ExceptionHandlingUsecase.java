package io.martins.valhalla.command;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ExceptionHandlingUsecase {

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    // update employee DB

    CompletableFuture<String> employeeDataFuture = CompletableFuture
        .supplyAsync(() -> {
          gracefullyShutDown( );
          return "Employee information update in DB";
        });
//        .exceptionally(_ -> {
//          System.out.println("unable to update employee information in DB");
//          return "500 Internal Server Error";
//        });

    CompletableFuture<String> employeeDocumentFuture = CompletableFuture
        .supplyAsync(() -> {
//          gracefullyShutDown();
          return "Employee document update in S3";
        });
//        .exceptionally(_ -> {
//          System.out.println("unable to update employee document in s3");
//          return "500 Internal Server Error";
//        });

    CompletableFuture<String> combineFuture = employeeDataFuture
        .thenCombine(employeeDocumentFuture, (result1, result2) -> result1 + "\n" + result2)
        //Global Exception Handling
        .handle((res, ex) -> {
          if (ex != null) {
            System.out.println("An error occurred during processing employee data " + ex.getMessage());
            return "Operation Failed ! ";
          }

          return res;
        });

    System.out.println(combineFuture.get());
  }

  private static void gracefullyShutDown() {
    throw new RuntimeException("Employee service temporarily unavailable. Please try again later.");
  }
}