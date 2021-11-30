package com.example.searchbusanshopapi.user.controller;

import com.example.searchbusanshopapi.user.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequiredArgsConstructor
public class MailControlelr {

    private final MailService mailService;

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
