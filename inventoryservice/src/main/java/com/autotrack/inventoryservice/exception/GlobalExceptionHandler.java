package com.autotrack.inventoryservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> methodArgsInvalid(MethodArgumentNotValidException e, HttpServletRequest request){
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldError = ((FieldError) error).getField();
            String errorMsg = error.getDefaultMessage();

            errors.put(fieldError, errorMsg);
        });

        log.warn("Validation errors caught : {} | {}",request.getRequestURI(), errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SparePartException.class)
    public ResponseEntity<Map<String, Object>> handleSparePartException(SparePartException ex, HttpServletRequest request) {

        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("timestamp", LocalDateTime.now().toString());
        errorBody.put("status", HttpStatus.BAD_REQUEST.value());
        errorBody.put("error", "Inventory Operation Failed");
        errorBody.put("message", ex.getMessage());

        errorBody.put("path", request.getRequestURI());
        return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }
}