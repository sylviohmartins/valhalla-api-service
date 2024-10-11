package io.martins.valhalla.exception;

import java.io.Serial;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ClientException extends BusinessException {

  @Serial
  private static final long serialVersionUID = 1L;

  public ClientException(final String message, final HttpStatus httpStatus) {
    super(message, httpStatus);
  }

}
