package com.example.playgame.exception.handler;

import com.example.playgame.dto.ExceptionDto;
import com.example.playgame.exception.*;
import com.example.playgame.exception.notfound.DevelopersGamesNotFoundException;
import com.example.playgame.exception.notfound.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler({
            EntityNotFoundException.class,
            DevelopersGamesNotFoundException.class
    })
    public ResponseEntity<ExceptionDto> handleNotFound(RuntimeException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionDto> handleAccessDenied(AccessDeniedException ex) {
        return buildErrorResponse("У вас недостаточно прав для выполнения этой операции.", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ExceptionDto> handleAuthenticationFailed(AuthenticationFailedException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ExceptionDto> handleEmailNotVerified(EmailNotVerifiedException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            AccountNullException.class,
            BucketNullException.class,
            InvalidVerificationTokenException.class
    })
    public ResponseEntity<ExceptionDto> handleBadRequest(RuntimeException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleValidationException(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildErrorResponse("Ошибка валидации: " + errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionDto> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        return buildErrorResponse(message, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleGeneralException(Exception ex) {
        log.error("Unexpected error", ex);
        return buildErrorResponse("Произошла непредвиденная ошибка сервера.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ExceptionDto> buildErrorResponse(String message, HttpStatus status) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .message(message)
                .time(ZonedDateTime.now())
                .build();
        return ResponseEntity.status(status).body(exceptionDto);
    }
}