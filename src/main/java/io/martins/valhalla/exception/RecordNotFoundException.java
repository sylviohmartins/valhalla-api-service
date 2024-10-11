package io.martins.valhalla.exception;

import io.martins.valhalla.constant.MessageConstants;
import java.io.Serial;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class RecordNotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  public RecordNotFoundException() {
    super(MessageConstants.RECORD_NOT_FOUND);
  }

  public RecordNotFoundException(final String message) {
    super(message);
  }

}
