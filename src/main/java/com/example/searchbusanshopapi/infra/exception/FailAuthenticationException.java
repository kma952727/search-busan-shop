package com.example.searchbusanshopapi.infra.exception;

import lombok.Getter;

@Getter
public class FailAuthenticationException extends RuntimeException {

    private Errorcode errorcode;
    private String username;

    public FailAuthenticationException(Errorcode errorCode, String username) {
        super();
        this.errorcode = errorCode;
        this.username = username;
    }
}