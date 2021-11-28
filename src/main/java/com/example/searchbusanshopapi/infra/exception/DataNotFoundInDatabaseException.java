package com.example.searchbusanshopapi.infra.exception;

import lombok.Getter;

@Getter
public class DataNotFoundInDatabaseException extends RuntimeException{

    private String targetData;
    private Errorcode errorcode;

    public DataNotFoundInDatabaseException(String targetData, Errorcode errorcode){
        super();
        this.targetData = targetData;
        this.errorcode = errorcode;
    }
}
