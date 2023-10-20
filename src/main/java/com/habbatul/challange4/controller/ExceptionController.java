package com.habbatul.challange4.controller;

import com.habbatul.challange4.model.responses.WebResponse;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ExceptionController {

    //bila nantinya ada exception ResponseStatusException arahkan kesini
    //saya tambah annotation @ResponseStatus biar muncul di doc, karena rata2 juga custom nya di bad_request
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<WebResponse<String>> adaException(ResponseStatusException error) {
        return ResponseEntity.status(
                error.getRawStatusCode())
                .body(WebResponse.
                        <String>builder()
                        .error(error.getReason())
                        .build()
                );
    }

}
