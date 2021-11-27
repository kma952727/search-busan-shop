package com.example.searchbusanshopapi.infra.exception;

import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException{

    Errorcode errorcode;

    public InvalidTokenException(Errorcode errorcode){
        super();
        this.errorcode = errorcode;
    }
}
