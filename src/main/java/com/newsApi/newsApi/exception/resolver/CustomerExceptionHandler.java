package com.newsApi.newsApi.exception.resolver;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

//@RestControllerAdvice
@Slf4j
public class CustomerExceptionHandler {
    static Map<String,Object> response = new HashMap<>();
//
//    @ExceptionHandler(ExpiredJwtException.class)
//    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException e){
//        response.put("ErrorMessage","Jwt expired");
//        response.put("status", 401);
//        return ResponseEntity.status(HttpStatus.valueOf(401)).body(response);
//    }
//    @ExceptionHandler(MalformedJwtException.class)
//    public ResponseEntity<?> handelMalFormedJwtException(MalformedJwtException e){
//        response.put("ErrorMessage","Jwt token has been tampered");
//        response.put("status", 400);
//        return ResponseEntity.status(HttpStatus.valueOf(400)).body(response);
//    }
//    @ExceptionHandler(value = IllegalArgumentException.class, exception = Exception.class)
//    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e){
//        response.put("ErrorMessage","Something went wrong. Please signin again");
//        response.put("status", 500);
//        return ResponseEntity.status(HttpStatus.valueOf(500)).body(response);
//    }
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e){
        log.error("error in receiving webhook data");
        response.put("ErrorMessage","error in receiving webhook data");
        response.put("status", 500);
        return ResponseEntity.status(HttpStatus.valueOf(500)).body(response);
    }
}
