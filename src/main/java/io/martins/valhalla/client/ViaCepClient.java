package io.martins.valhalla.client;

import io.martins.valhalla.constant.MessageConstants;
import io.martins.valhalla.domain.dto.nested.EnderecoDTO;
import io.martins.valhalla.exception.ClientException;
import io.netty.handler.timeout.ReadTimeoutException;
import java.net.ConnectException;
import java.time.Duration;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Component
public class ViaCepClient {

  private final WebClient webClient;

  public ViaCepClient(@Value("${application.api.viacep.url}") final String viaCepUrl) {
    final HttpClient httpClient = HttpClient.create() //
        .responseTimeout(Duration.ofSeconds(5));

    this.webClient = WebClient.builder() //
        .baseUrl(viaCepUrl) //
        .clientConnector(new ReactorClientHttpConnector(httpClient)) //
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) //
        .build();
  }

  @Retryable( //
      retryFor = {ReadTimeoutException.class, ConnectException.class, ClientException.class}, //
      maxAttemptsExpression = "${application.retry.max-attempts}", //
      backoff = @Backoff(maxDelayExpression = "${application.retry.max-delay}") //
  )
  public Optional<EnderecoDTO> findEnderecoByCep(final String cep) {
    return webClient.get() //
        .uri("/{cep}/json", cep) //
        .retrieve() //
        .bodyToMono(EnderecoDTO.class) //
        .onErrorResume(e -> Mono.error(new ClientException(MessageConstants.ERRO_SERVIDOR_VIACEP, HttpStatus.INTERNAL_SERVER_ERROR))) //
        .flatMap(endereco -> endereco.cep() == null ? Mono.empty() : Mono.just(endereco)) //
        .blockOptional();
  }

}
