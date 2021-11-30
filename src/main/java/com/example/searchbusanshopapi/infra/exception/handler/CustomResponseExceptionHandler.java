package com.example.searchbusanshopapi.infra.exception.handler;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.example.searchbusanshopapi.infra.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;

@RestController
@ControllerAdvice
public class CustomResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SignatureVerificationException.class)
    private final ResponseEntity<Object> handleAllException(Exception ex,
                                                            WebRequest request){
        ExceptionResponse exceptionResponse = ExceptionResponse
                .builder()
                .timestamp(new Date())
                .build();

        return new ResponseEntity(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFoundException.class)
    private final ResponseEntity<Object> handleNotFoundUserInRepositoryException(UserNotFoundException ex,
                                                                                 WebRequest request){
        String message;
        if(ex.getId() == null) {
            message = "아무 사용자가 없습니다.";
        }else{
            message = String.format("id:[%s]에 해당하는 사용자가 없습니다.", ex.getId());
        }

        ExceptionResponse exceptionResponse =
                ExceptionResponse.builder()
                        .timestamp(new Date())
                        .message(message)
                        .statusDetail(ex.getErrorcode().toString())
                        .requestDetail(request.toString())
                        .build();

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RegistedUsernameException.class)
    private final ResponseEntity<Object> handleRegistUserException(RegistedUsernameException ex, WebRequest webRequest){

        ExceptionResponse exceptionResponse =
                ExceptionResponse.builder().timestamp(new Date())
                        .message("")
                        .statusDetail(ex.getErrorCode().toString()).build();


        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FailAuthenticationException.class)
    private final ResponseEntity<Object> handleAuthenticationException(FailAuthenticationException ex, WebRequest request){

        ExceptionResponse exceptionResponse =
                ExceptionResponse.builder().timestamp(new Date())
                        .message(String.format("로그인을 다시 진행해주세요."))
                        .statusDetail(ex.getErrorcode().toString())
                        .requestDetail(request.toString()).build();

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidTokenException.class)
    public final ResponseEntity handleAccessDeniedException(InvalidTokenException ex, WebRequest request) {

        ExceptionResponse exceptionResponse =
                ExceptionResponse.builder().timestamp(new Date())
                        .message(String.format(ex.getMessage()))
                        .statusDetail(ex.getErrorcode().toString())
                        .requestDetail(request.toString() + "/ 토큰값 : "+ex.getToken()).build();
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataNotFoundInDatabaseException.class)
    public final ResponseEntity handleNoDataInDatabase(DataNotFoundInDatabaseException ex,
                                                       WebRequest request){
        ExceptionResponse exceptionResponse =
                ExceptionResponse.builder().timestamp(new Date())
                        .message("데이터베이스내에서 해당 값들을 찾을수없습니다")
                        .statusDetail(ex.getErrorcode().toString())
                        .requestDetail(request.toString() + " / "+ex.getTargetData()).build();
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DuplicatedKeyException.class)
    public final ResponseEntity handleDuplicateKey(DuplicatedKeyException ex, WebRequest request){
        ExceptionResponse exceptionResponse =
        ExceptionResponse.builder().timestamp(new Date())
                .message("디비내에 이미 값이 있습니다.(유니크키)")
                .statusDetail(ex.getErrorcode().toString())
                .requestDetail(request.toString() + "/ " + ex.getDuplicateMessage()).build();
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
