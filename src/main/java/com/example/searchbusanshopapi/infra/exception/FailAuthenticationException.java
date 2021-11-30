package com.example.searchbusanshopapi.infra.exception;

import lombok.Getter;

@Getter
public class FailAuthenticationException extends RuntimeException {

    private Errorcode errorcode;

    public FailAuthenticationException(Errorcode errorCode) {
        super();
        this.errorcode = errorCode;
    }
}