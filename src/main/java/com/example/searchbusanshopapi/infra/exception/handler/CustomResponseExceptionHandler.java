package com.example.searchbusanshopapi.infra.exception.handler;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.example.searchbusanshopapi.infra.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

/**
 * 스프링영역, 시큐리티영역 전역적으로 생겨나는 예외를
 * 처리하는 클래스입니다.
 * 시큐리티영역은 로그인익셉션컨트롤러, 토큰체크핸들러, 커스텀액세스디나인핸들러를 타고 옵니다.
 */
@RestController
@ControllerAdvice
public class CustomResponseExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 토큰유효성이 올바르지않을경우 사용합니다.
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(SignatureVerificationException.class)
    private final ResponseEntity<Object> handleAllException(Exception ex,
                                                            WebRequest request){
        ExceptionResponse exceptionResponse = ExceptionResponse
                .builder()
                .timestamp(new Date())
                .build();

        return new ResponseEntity(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 사용자조회시 아무사용자도 찾을수 없을경우 사용합니다.
     * @param ex
     * @param request
     * @return
     */
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

    /**
     * 회원가입할시 이미 등록된 유저네임일경우 사용합니다.
     * @param ex
     * @param webRequest
     * @return
     */
    @ExceptionHandler(RegistedUsernameException.class)
    private final ResponseEntity<Object> handleRegistUserException(RegistedUsernameException ex, WebRequest webRequest){

        ExceptionResponse exceptionResponse =
                ExceptionResponse.builder().timestamp(new Date())
                        .message("")
                        .statusDetail(ex.getErrorCode().toString()).build();


        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 로그인과정에서 인증실패하였을경우 사용합니다.
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(FailAuthenticationException.class)
    private final ResponseEntity<Object> handleAuthenticationException(FailAuthenticationException ex, WebRequest request){

        ExceptionResponse exceptionResponse =
                ExceptionResponse.builder().timestamp(new Date())
                        .message(String.format("로그인을 다시 진행해주세요."))
                        .statusDetail(ex.getErrorcode().toString())
                        .requestDetail(request.toString()).build();

        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 토큰의 값이 올바르지 않을경우 사용합니다.
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(InvalidTokenException.class)
    public final ResponseEntity handleInvalidTokenException(InvalidTokenException ex, WebRequest request) {

        ExceptionResponse exceptionResponse =
                ExceptionResponse.builder().timestamp(new Date())
                        .message(String.format(ex.getMessage()))
                        .statusDetail(ex.getErrorcode().toString())
                        .requestDetail(request.toString() + "/ 토큰값 : "+ex.getToken()).build();
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * db내에서 값을 찾을수없는경우 사용합니다.
     * @param ex
     * @param request
     * @return
     */
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

    /**
     * db내의 키가 중복되었을시 사용합니다.
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(DuplicatedKeyException.class)
    public final ResponseEntity handleDuplicateKey(DuplicatedKeyException ex, WebRequest request){
        ExceptionResponse exceptionResponse =
        ExceptionResponse.builder().timestamp(new Date())
                .message("디비내에 이미 값이 있습니다.(유니크키)")
                .statusDetail(ex.getErrorcode().toString())
                .requestDetail(request.toString() + "/ " + ex.getDuplicateMessage()).build();
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 인증주체자가 시큐리티 필터단에서 이용하고자하는 엔드포인트의 필요권한을 가지지 못할경우
     * 호출됩니다.
     * 경로 : CustomAccessDeniedHandler -> LoginExceptionController -> 현재
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity handleAccessDenied(AccessDeniedException ex, WebRequest request){

        ExceptionResponse exceptionResponse =
                ExceptionResponse.builder().timestamp(new Date())
                        .message("이용할수 없는 권한입니다.")
                        .statusDetail(Errorcode.ACCESS_DINIED.toString())
                        .requestDetail(request.toString() + " / " + ex.toString()).build();
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
