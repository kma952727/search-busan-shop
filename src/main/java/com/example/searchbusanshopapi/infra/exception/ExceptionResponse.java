package com.example.searchbusanshopapi.infra.exception;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class ExceptionResponse {
    private Date timestamp;
    private String message;
    private String statusDetail;
    private String requestDetail;

}
