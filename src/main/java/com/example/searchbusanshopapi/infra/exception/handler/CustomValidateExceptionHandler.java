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

/**
 * 스프링영역, 시큐리티영역 전역적으로 생기는 예외를
 * 처리하는 클래스입니다.
 * 시큐리티영역은 로그인익셉션컨트롤러, 토큰체크핸들러, 커스텀액세스디나인핸들러를 타고 옵니다.
 */
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

    /**
     * 요청시 넘어오는 파라미터, 바디의 내용이
     * 유효하지 않을시 사용합니다.
     * @param ex
     * @param request
     * @return
     */
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
