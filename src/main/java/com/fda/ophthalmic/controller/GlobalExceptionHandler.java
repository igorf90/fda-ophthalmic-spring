package com.fda.ophthalmic.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
@RestControllerAdvice public class GlobalExceptionHandler {
 @ExceptionHandler(ConstraintViolationException.class)
 public ResponseEntity<Map<String,String>> validation(ConstraintViolationException ex){return ResponseEntity.badRequest().body(Map.of("error","Invalid identifier"));}
 @ExceptionHandler(IllegalArgumentException.class)
 public ResponseEntity<Map<String,String>> invalid(IllegalArgumentException ex){return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error",ex.getMessage()));}
}
