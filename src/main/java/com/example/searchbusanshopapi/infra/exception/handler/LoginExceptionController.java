package com.example.searchbusanshopapi.infra.exception.handler;

import com.example.searchbusanshopapi.infra.exception.Errorcode;
import com.example.searchbusanshopapi.infra.exception.FailAuthenticationException;
import com.example.searchbusanshopapi.infra.exception.InvalidTokenException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class LoginExceptionController {

    @GetMapping("/jwt/authentication")
    public void noAuthentication(@RequestParam String username){
        throw new FailAuthenticationException(Errorcode.NO_MATCHING_AUTHENTICATION_IN_DB);
    }

    @GetMapping("/error")
    public void authenticationError(HttpServletRequest request, HttpServletResponse response){
        throw new FailAuthenticationException(Errorcode.NO_MATCHING_AUTHENTICATION_IN_DB);
    }
}
