package com.example.searchbusanshopapi.infra.exception.handler;

import com.example.searchbusanshopapi.infra.exception.Errorcode;
import com.example.searchbusanshopapi.infra.exception.FailAuthenticationException;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginExceptionController {

    @GetMapping("/jwt/authentication")
    public void noAuthentication(@RequestParam String username){
        throw new FailAuthenticationException(Errorcode.NO_MATCHING_AUTHENTICATION_DATABASE, username);
    }
}
