package io.martins.valhalla.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationConfiguration {

  @Valid
  private final Metadata metadata = new Metadata();

  @Getter
  @Setter
  public static class Metadata {

    @NotNull
    private URL url;

    @NotBlank
    private String name;

    @NotBlank
    private String mailFrom;

  }

}
