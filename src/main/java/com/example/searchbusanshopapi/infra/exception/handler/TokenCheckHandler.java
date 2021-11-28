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

@Component
public class TokenCheckHandler implements HandlerInterceptor {

    private Set blackFilter = new HashSet();

    public TokenCheckHandler() {
        blackFilter.add("/users"); // 회원가입 url
        blackFilter.add("/jwt/authentication"); // 로그인 url
        blackFilter.add("/v3/api-docs"); //swagger url
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(response.getHeader("success") == null){
            return true;
        }
        System.out.println(request.getServletPath()+ "   ***");
        if(blackFilter.contains(request.getServletPath())){
            return true;
        }

        String token = response.getHeader("token");
        if(token == null) {
            token = "토큰이 존재하지 않습니다.";
        }
        throw new InvalidTokenException(Errorcode.INVALID_TOKEN, response.getHeader("cause"), token);

    }
}