package io.martins.valhalla.exception.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class RestResponse {

  @Builder.Default
  private final LocalDateTime timestamp = LocalDateTime.now();

  private String type;

  private int status;

  private String error;

  private String message;

  @JsonInclude(value = Include.NON_NULL)
  private List<FieldError> fieldErrors;

  private String detail;

  private String path;

  private String help;

  public static class RestResponseBuilder {

    public RestResponseBuilder status(final HttpStatus status) {
      this.status = status.value();

      if (status.isError()) {
        error = status.getReasonPhrase();
      }

      return this;
    }

    public RestResponseBuilder exception(final Exception exception) {
      type = exception.getClass().getSimpleName();
      error = exception.getMessage();
      message = exception.getMessage();

      if (exception.getCause() != null) {
        detail = exception.getCause().toString();

      } else if (exception.getMessage() != null && !exception.getMessage().equals(exception.getLocalizedMessage())) {
        detail = exception.getLocalizedMessage();
      }

      return this;
    }

    public ResponseEntity<RestResponse> entity() {
      return ResponseEntity.status(status).headers(HttpHeaders.EMPTY).body(build());
    }

  }

  @Getter
  @AllArgsConstructor
  public static class FieldError {

    private String iid;

    private String entity;

    private String field;

    private String error;

    public FieldError(final String field, final String error) {
      this.field = field;
      this.error = error;
    }

  }

}
