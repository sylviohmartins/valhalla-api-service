package io.martins.valhalla.exception.handler;

import io.martins.valhalla.constant.MessageConstants;
import io.martins.valhalla.exception.AlreadyExistsException;
import io.martins.valhalla.exception.BusinessException;
import io.martins.valhalla.exception.RecordNotFoundException;
import io.martins.valhalla.exception.handler.RestResponse.FieldError;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  private static final String GROUP_ID = "io.martins";

  @ExceptionHandler(BusinessException.class)
  ResponseEntity<RestResponse> handleBusiness(final WebRequest request, final BusinessException exception) {
    String message = getMessage(exception.getMessage());
    List<RestResponse.FieldError> fieldErrors = Collections.emptyList();

    return handleInvalidParamException(request, exception.getHttpStatus(), exception, message, fieldErrors);
  }

  @ExceptionHandler(RecordNotFoundException.class)
  ResponseEntity<RestResponse> handleNotFound(final WebRequest request, final RecordNotFoundException exception) {
    String message = getMessage(exception.getMessage());
    List<RestResponse.FieldError> fieldErrors = Collections.emptyList();

    return handleInvalidParamException(request, HttpStatus.NOT_FOUND, exception, message, fieldErrors);
  }

  @ExceptionHandler(AlreadyExistsException.class)
  ResponseEntity<RestResponse> handleAlreadyExists(final WebRequest request, final AlreadyExistsException exception) {
    String message = getMessage(exception.getMessage());
    List<RestResponse.FieldError> fieldErrors = Collections.emptyList();

    return handleInvalidParamException(request, HttpStatus.BAD_REQUEST, exception, message, fieldErrors);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid( //
      final MethodArgumentNotValidException exception, //
      final HttpHeaders headers, //
      final HttpStatusCode status, //
      final WebRequest request //
  ) {
    String message = getMessage(MessageConstants.INVALID_FIELDS);
    List<FieldError> fieldErrors = new ArrayList<>();

    for (org.springframework.validation.FieldError error : exception.getBindingResult().getFieldErrors()) {
      fieldErrors.add(new FieldError(error.getField(), error.getDefaultMessage()));
    }

    return handleInvalidParamException(request, HttpStatus.BAD_REQUEST, exception, message, fieldErrors);
  }

  @SuppressWarnings("unchecked")
  private <T> ResponseEntity<T> handleInvalidParamException( //
      final WebRequest request, //
      final HttpStatus status, //
      final Exception exception, //
      final String message, //
      final List<RestResponse.FieldError> fieldErrors //
  ) {
    return (ResponseEntity<T>) RestResponse.builder() //
        .exception(exception) //
        .status(status) //
        .message(message) //
        .fieldErrors(fieldErrors) //
        .path(getPath(request)) //
        .entity();
  }

  private String getPath(final WebRequest request) {
    return request.getDescription(false).substring(4);
  }

  private String getMessage(final String message) {
    MessageSource messageSource = getMessageSource();

    if (StringUtils.startsWithIgnoreCase(message, GROUP_ID) && messageSource != null) {
      return messageSource.getMessage(message, null, LocaleContextHolder.getLocale());
    }

    return message;
  }

}
