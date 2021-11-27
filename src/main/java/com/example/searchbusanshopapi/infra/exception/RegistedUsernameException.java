package com.example.searchbusanshopapi.infra.exception;

import lombok.Getter;

@Getter
public class RegistedUsernameException extends RuntimeException{

    private Errorcode errorCode;
    private String username;

    public RegistedUsernameException(Errorcode errorCode, String username){
        super();
        this.errorCode = errorCode;
        this.username = username;
    }
}
