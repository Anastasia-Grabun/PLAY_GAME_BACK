package com.example.playgame.exception.handler;

import com.example.playgame.dto.ExceptionDto;
import com.example.playgame.exception.*;
import com.example.playgame.exception.notfound.DevelopersGamesNotFoundException;
import com.example.playgame.exception.notfound.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

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

    @ExceptionHandler({
            InsufficientFundsException.class,
            InvalidAmountException.class,
            IllegalArgumentException.class,
            AccountNullException.class,
            BucketNullException.class
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleGeneralException(Exception ex) {
        log.error("Unexpected error", ex);
        return buildErrorResponse("Произошла непредвиденная ошибка сервера.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ExceptionDto> buildErrorResponse(String message, HttpStatus status) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .message(message)
                .build();
        return ResponseEntity.status(status).body(exceptionDto);
    }
}