package com.example.searchbusanshopapi.user.controller;

import com.example.searchbusanshopapi.user.service.MailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@Api(description = "메일을 활용한 인증기능을 제공합니다.")
@RestController
@RequiredArgsConstructor
public class MailControlelr {

    private final MailService mailService;

    /**
     * 회원에게 인증메일을 보냅니다.
     * @param userId 인증메일 대상자
     * @return
     * @throws MessagingException
     */
    @ApiOperation(value = "인증메일 발송")
    @PostMapping("/users/{userId}/mail/authentication")
    public ResponseEntity sendAuthenticationMail(@PathVariable Long userId) throws MessagingException {
        try {
            mailService.sendAuthenticationMail(userId);
        }catch (MessagingException e){
            e.printStackTrace();
            throw e;
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 인증메일내에 들어있는 링크를 누를경우 호출됩니다.
     * 보낼때 db에 넣은 토큰값과 비교합니다.
     * @param userId
     * @param mailToken
     * @return
     */
    @ApiOperation(value = "인증메일 토큰 검사")
    @GetMapping("/users/{userId}/mail/authentication")
    public ResponseEntity checkAuthenticationMail(@PathVariable Long userId,
                                                  @RequestParam String mailToken){

        boolean checkResult = mailService.checkAuthenticationMail(userId, mailToken);
        if(!checkResult) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
