package com.example.portalaluno.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED) // Define que o erro será 401 automaticamente
public class ContaInativaException extends RuntimeException {
    public ContaInativaException(String message) {
        super(message);
    }
}