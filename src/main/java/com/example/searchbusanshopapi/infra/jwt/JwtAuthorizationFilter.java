package com.example.searchbusanshopapi.infra.jwt;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.searchbusanshopapi.infra.auth.CustomUserDetails;
import com.example.searchbusanshopapi.infra.exception.InvalidTokenException;
import com.example.searchbusanshopapi.infra.redis.RedisService;
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

/**
 * 토큰의 유효성을 검사합니다.
 *
 * 순서
 *
 * 1. /user(POST)로 접근하였나?
 * 2. 헤더에 값이 없는가?
 * 3-1. 만료시간이 지났는가?
 * 3-2. 토큰의 형태가 이상한가?
 * 3-3. 토큰이 redis에 올라와있을경우, 로그아웃토큰으로간주
 * 4. 토큰에서 추출한 username이 db에 없는가?
 * 5. 유효성 검사 통과
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final RedisService redisService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, JwtService jwtService, RedisService redisService) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.redisService = redisService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //1. POST=/users(로그인) 으로 접근하였을시 토큰검증로직을 통과합니다.
        if(request.getServletPath().equals("/jwt/authentication")){
            Authentication authentication
                    = new UsernamePasswordAuthenticationToken(null, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
            return;
        }

        //헤더에서 토큰 분리
        String jwtHeader = request.getHeader("Authorization");

        //2. 헤더에서 뽑은값이 없거나, Bearer이 없을시 실패로 간주
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            response.addHeader("token", jwtHeader);
            response.addHeader("success", "false");
            response.addHeader("cause", "토큰을 찾지못하였습니다.");
            chain.doFilter(request, response);
            return;
        }



        String username = null;
        try {
            username = jwtService.verifyToken(jwtHeader);
            redisService.isBlockToken(jwtHeader);
        }catch (TokenExpiredException e){
            //3-1. 토큰을 검증하였으나 만료시간이 지났을경우, /jwt/authentication/refresh로 클라이언트에서 다시 호출
            e.printStackTrace();
            response.addHeader("token", jwtHeader);
            response.addHeader("success", "false");
            response.addHeader("cause", "토큰의 수명이 다했습니다.");
            redisService.removeToken(jwtHeader);
            chain.doFilter(request, response);
            return;
        }catch (SignatureVerificationException e){
            //3-2. 토큰을 검증하였으나 토큰형태가 이상할경우
            e.printStackTrace();
            response.addHeader("token", jwtHeader);
            response.addHeader("success", "false");
            response.addHeader("cause", "토큰은 존재하나 올바르지 않습니다.");
            chain.doFilter(request, response);
            return;
            //3-3 token이 redis에 올라와있을경우, 로그아웃한토큰으로 간주
        }catch (InvalidTokenException e) {
            e.printStackTrace();
            response.addHeader("token", jwtHeader);
            response.addHeader("success", "false");
            response.addHeader("cause", "해당토큰은 차단되었습니다. 다시 로그인해주세요.");
            chain.doFilter(request, response);
            return;
        }

        User userEntity = userRepository.findByUsername(username);
        if(userEntity == null ){
            //4. 토큰이 검증되고 올바르나 추출한 username이 db에 없을경우
            response.addHeader("token", jwtHeader);
            response.addHeader("success", "false");
            response.addHeader("cause", "토큰은 존재하나 올바르지 않습니다.");
            chain.doFilter(request, response);
            return;
        }
        //5. 유효성 검사 통과
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
