package com.example.searchbusanshopapi.infra.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@AllArgsConstructor
public enum Errorcode {

    USER_NOT_FOUND_IN_DB(HttpStatus.NOT_FOUND, "1"),
    ALREADY_REGISTED_USERNAME_IN_DB(HttpStatus.BAD_REQUEST, "2"),
    NO_MATCHING_AUTHENTICATION_DATABASE(HttpStatus.BAD_REQUEST, "3"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "4");

    private HttpStatus httpStatus;
    private String code;
}
