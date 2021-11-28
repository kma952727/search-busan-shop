package com.example.searchbusanshopapi.infra.jwt;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.searchbusanshopapi.infra.auth.CustomUserDetails;
import com.example.searchbusanshopapi.user.model.User;
import com.example.searchbusanshopapi.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, JwtService jwtService) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //헤더에서 토큰 분리
        String jwtHeader = request.getHeader("Authorization");

        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            response.addHeader("token", jwtHeader);
            response.addHeader("success", "false");
            response.addHeader("cause", "토큰을 찾지못하였습니다.");
            chain.doFilter(request, response);
            return;
        }

        //jwt 토큰유효성 확인
        String username = null;
        try {
            username = jwtService.verifyToken(jwtHeader);
        }catch (TokenExpiredException e){
            e.printStackTrace();
            response.addHeader("token", jwtHeader);
            response.addHeader("success", "false");
            response.addHeader("cause", "토큰의 수명이 다했습니다.");
            chain.doFilter(request, response);
            return;
        }catch (SignatureVerificationException e){
            e.printStackTrace();
            response.addHeader("token", jwtHeader);
            response.addHeader("success", "false");
            response.addHeader("cause", "토큰은 존재하나 올바르지 않습니다.");
            chain.doFilter(request, response);
            return;
        }

        User userEntity = userRepository.findByUsername(username);
        CustomUserDetails userDetails =
                new CustomUserDetails(userEntity);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }
}
