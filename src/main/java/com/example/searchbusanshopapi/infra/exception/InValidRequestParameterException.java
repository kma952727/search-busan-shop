package com.example.searchbusanshopapi.infra.exception;

import lombok.Getter;

@Getter
public class InValidRequestParameterException extends RuntimeException{

    private String message;

    public InValidRequestParameterException(String message){
        super();
        this.message = message;
    }
}
