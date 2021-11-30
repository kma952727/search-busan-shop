package com.example.searchbusanshopapi.infra.jwt;

import com.example.searchbusanshopapi.infra.auth.CustomUserDetails;
import com.example.searchbusanshopapi.user.model.RefreshToken;
import com.example.searchbusanshopapi.user.model.User;
import com.example.searchbusanshopapi.user.repository.RefreshRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.util.UUID;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;
    private String username;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService, RefreshRepository refreshRepository){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        User user = null;

        try {
            ObjectMapper om = new ObjectMapper();
            user = om.readValue(request.getInputStream(), User.class);

            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            username = user.getUsername();
            Authentication authentication = authenticationManager.authenticate(token);

            return authentication;
        }catch (IOException e) {
            e.printStackTrace();
            Authentication falseLogin = new UsernamePasswordAuthenticationToken("", "", null);
            Authentication authentication = authenticationManager.authenticate(falseLogin);
            return authentication;
        }catch (InternalAuthenticationServiceException e){
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {

        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authResult);
        String username = userDetails.getUsername();

        String jwtToken = jwtService.createToken(username);
        String tokenValue = jwtService.createRefreshToken(username, UUID.randomUUID().toString());

        RefreshToken refreshToken = getRefreshToken(tokenValue, username);
        RefreshToken OldRefreshToken = refreshRepository.findByUsername(username);
        if(OldRefreshToken != null) {
            OldRefreshToken.setTokenValue(refreshToken.getTokenValue());
            refreshRepository.save(OldRefreshToken);
        }else{
            refreshRepository.save(refreshToken);
        }
        response.addHeader("refresh", "Bearer "+refreshToken.getTokenValue());
        response.addHeader("Authorization", "Bearer "+jwtToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.sendRedirect("/jwt/authentication?username="+username);
    }

    private RefreshToken getRefreshToken(String tokenValue, String username){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenValue(tokenValue);
        refreshToken.setUsername(username);
        return refreshToken;
    }
}
