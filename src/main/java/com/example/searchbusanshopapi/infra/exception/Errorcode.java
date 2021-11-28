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
    NO_MATCHING_AUTHENTICATION_IN_DB(HttpStatus.BAD_REQUEST, "3"),
    NO_MATCHING_REQUEST_DATA_IN_DB(HttpStatus.BAD_REQUEST,"4"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "5"),

    INVALID_PARAMETER_OR_BODY(HttpStatus.BAD_REQUEST ,"10");

    private HttpStatus httpStatus;
    private String code;
}
