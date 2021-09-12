package com.bwalczak.note.exception;

import com.bwalczak.note.domain.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import static com.bwalczak.note.Utils.DateUtil.dateTimeFormatter;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler implements ErrorController {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(exception.getMessage());

        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String fieldsMessage = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return new ResponseEntity<>(HttpResponse.builder()
                .status(status)
                .statusCode(status.value())
                .reason("Invalid fields: " + fieldsMessage)
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build(), status);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(HttpResponse.builder()
                .status(status)
                .statusCode(status.value())
                .reason(exception.getMessage())
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build(), status);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<HttpResponse<?>> illegalStateException(IllegalStateException exception) {
        return createHttpErrorResponse(BAD_REQUEST, exception.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<HttpResponse<?>> noteNotFoundException(NoteNotFoundException exception) {
        return createHttpErrorResponse(BAD_REQUEST, exception.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse<?>> noResultException(NoResultException exception) {
        return createHttpErrorResponse(BAD_REQUEST, exception.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ServletException.class)
    public ResponseEntity<HttpResponse<?>> servletException(ServletException exception) {
        return createHttpErrorResponse(BAD_REQUEST, exception.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse<?>> exception(Exception exception) {
        return createHttpErrorResponse(BAD_REQUEST, exception.getMessage());
    }

    private ResponseEntity<HttpResponse<?>> createHttpErrorResponse(HttpStatus httpStatus, String reason) {
        log.error(reason);
        return new ResponseEntity<>(HttpResponse.builder()
                .status(httpStatus)
                .statusCode(httpStatus.value())
                .reason("Internal error occured: " + reason)
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build(), httpStatus);
    }
}
