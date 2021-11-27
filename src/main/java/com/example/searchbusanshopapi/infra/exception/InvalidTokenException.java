package com.example.searchbusanshopapi.infra.exception;

import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException{

    Errorcode errorcode;
    String message;
    String token;

    public InvalidTokenException(Errorcode errorcode, String message,String token){
        super();
        this.errorcode = errorcode;
        this.message = message;
        this.token = token;
    }
}
