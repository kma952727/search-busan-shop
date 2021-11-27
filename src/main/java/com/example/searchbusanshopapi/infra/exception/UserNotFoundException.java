package com.example.searchbusanshopapi.infra.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException{
    private final Errorcode errorcode;
    private Long id;

    public UserNotFoundException(Errorcode errorCode){
        this.errorcode = errorCode;
    }

    public UserNotFoundException(Errorcode errorCode, Long id){
        super();
        this.errorcode = errorCode;
        this.id = id;
    }
}
