package com.example.searchbusanshopapi.infra.exception.handler;

import com.example.searchbusanshopapi.infra.exception.Errorcode;
import com.example.searchbusanshopapi.infra.exception.ExceptionResponse;
import com.example.searchbusanshopapi.infra.exception.InValidRequestParameterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Date;

@RestController
@ControllerAdvice
public class CustomValidateExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleInvalidException(ConstraintViolationException ex,
                                                 WebRequest request){

        ExceptionResponse exceptionResponse =
                ExceptionResponse.builder()
                        .timestamp(new Date())
                        .message(ex.getMessage())
                        .statusDetail(Errorcode.INVALID_PARAMETER_OR_BODY.toString())
                        .requestDetail(request.toString())
                        .build();
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InValidRequestParameterException.class)
    public ResponseEntity handleUserJoinException(InValidRequestParameterException ex,
                                                  WebRequest request){

        ExceptionResponse exceptionResponse =
                ExceptionResponse.builder()
                        .timestamp(new Date())
                        .message(ex.getMessage())
                        .statusDetail(Errorcode.INVALID_PARAMETER_OR_BODY.toString())
                        .requestDetail(request.toString())
                        .build();
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
