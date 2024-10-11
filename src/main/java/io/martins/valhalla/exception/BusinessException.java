package io.martins.valhalla.exception;

import java.io.Serial;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
@Getter
public class BusinessException extends Exception {

  @Serial
  private static final long serialVersionUID = 1L;

  private final HttpStatus httpStatus;

  public BusinessException(final String message) {
    this(message, HttpStatus.BAD_REQUEST);
  }

  public BusinessException(final String message, final HttpStatus httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
  }

}
