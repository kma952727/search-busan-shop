package com.example.searchbusanshopapi.infra.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;

@Getter
@Setter
public class DuplicatedKeyException extends RuntimeException{

    private Errorcode errorcode;
    private String duplicateMessage;

    public DuplicatedKeyException(String duplicateMessage, Errorcode errorcode){
        super();
        this.duplicateMessage = duplicateMessage;
        this.errorcode = errorcode;
    }
}
