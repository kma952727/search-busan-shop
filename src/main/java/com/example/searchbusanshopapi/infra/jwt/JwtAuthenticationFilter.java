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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.util.UUID;

/**
 * 로그인시 거쳐가는 필터입니다.
 * 로그인방식으로 성공시 JWT(엑세스 + 리프레시)를 발급합니다.
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService, RefreshRepository refreshRepository){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        User user = null;

        try {
            //request의 body부분(json format)의 값을 userEntity에 매핑합니다.
            ObjectMapper om = new ObjectMapper();
            user = om.readValue(request.getInputStream(), User.class);

            //provider manager에 보내 db에 일치하는 값이 있는지 확인합니다.
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            Authentication authentication = authenticationManager.authenticate(token);

            return authentication;
        }catch (IOException e) {
            //정상적이지 않은 값일경우 정보가없는 authetication을 반환합니다.
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
        //성공할경우 securityContext에 넣습니다. 이후 뒤쪽필터에서 권한확인시 사용됩니다.
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authResult);
        String username = userDetails.getUsername();

        //발급할 토큰을 생성합니다.
        String accessToken = jwtService.createToken(username);
        String tokenValue = jwtService.createRefreshToken(username, UUID.randomUUID().toString());

        RefreshToken refreshToken = getRefreshToken(tokenValue, username);
        RefreshToken OldRefreshToken = refreshRepository.findByUsername(username);
        /**
         * 이미 로그인되어있는 경우 리프레시토큰을 재발급 해줍니다.
         * --
         *
         * 주석을 달며 생각해보니 로그인되어 있는데 다시 로그인을 할수가 없습니다.
         * 기록을 위해 남겨둡니다.
         */
        if(OldRefreshToken != null) {
            OldRefreshToken.setTokenValue(refreshToken.getTokenValue());
            refreshRepository.save(OldRefreshToken);
        }else{
            refreshRepository.save(refreshToken);
        }

        //리프레시, 엑세스토큰을 헤더에 넣습니다.
        response.addHeader("refresh", "Bearer "+refreshToken.getTokenValue());
        response.addHeader("Authorization", "Bearer "+accessToken);
    }

    /**
     * 로직실패시 요청url을 변경합니다.
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.sendRedirect("/jwt/authentication");
    }

    /**
     * refreshToken Entity를 만듭니다.
     * @param tokenValue refreshToken 값
     * @param username 테이블에 들어갈 refresh테이블의 키값
     * @return entity
     */
    private RefreshToken getRefreshToken(String tokenValue, String username){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenValue(tokenValue);
        refreshToken.setUsername(username);
        return refreshToken;
    }
}
