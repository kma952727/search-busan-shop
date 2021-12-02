package com.example.searchbusanshopapi.infra.exception.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ExceptionTranslationFilter에서
 * 권한투표로 접근할수없음을 받으면
 * 해당클래스의 hadnle()메서드가 호출됩니다.
 *
 * 토큰유효성검사를 하며 시큐리티컨텍스트에 들어간
 * 인증주체자의 값에서 권한부분을 추출하여 클라이언트에게 보여줍니다.
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = ((UserDetails)authentication.getPrincipal()).getAuthorities().toString();
        //url에 허용되지 않는 특수문자를 제거합니다.
        role = role.replace("[", "");
        role = role.replace("]", "");
        response.sendRedirect("/denied?cause="+role);
    }
}
