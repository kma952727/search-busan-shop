package com.example.searchbusanshopapi.infra.exception.handler;

import antlr.Token;
import com.example.searchbusanshopapi.infra.exception.Errorcode;
import com.example.searchbusanshopapi.infra.exception.InvalidTokenException;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 토큰체크결과를 예외처리로직에 넣을지 결정
 * blackFilter에 넣은 문자열은 로직에서 제외한다.
 */
@Component
public class TokenCheckHandler implements HandlerInterceptor {

    private final static String SUCCESS = "success";
    private final static String TOKEN = "token";
    private final static String CAUSE = "cause";
    private Set blackFilter = new HashSet();

    public TokenCheckHandler() {
        blackFilter.add("/users"); // 회원가입 url
        blackFilter.add("/jwt/authentication"); // 로그인 url
        blackFilter.add("/v3/api-docs"); //swagger url
    }

    /**
     * 필터를 모두 통과한후 커스텀필터에서 실패여부를 헤더에 넣었습니다.
     * 혹은 검증이필요없는 url일 경우 모두 정상적으로 true를 반환하여
     * 로직을 수행합니다.
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        if(response.getHeader(SUCCESS) == null){
            return true;
        }
        if(blackFilter.contains(request.getServletPath())){
            return true;
        }
        String token = response.getHeader(TOKEN);
        if(token == null) {
            token = "토큰이 존재하지 않습니다.";
        }

        //모두통과시 잘못된토큰으로 간주 에러처리합니다.
        throw new InvalidTokenException(Errorcode.INVALID_TOKEN, response.getHeader(CAUSE), token);

    }
}