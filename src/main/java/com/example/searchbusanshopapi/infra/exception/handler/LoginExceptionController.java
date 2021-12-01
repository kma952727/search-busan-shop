package com.example.searchbusanshopapi.infra.exception.handler;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.searchbusanshopapi.infra.exception.Errorcode;
import com.example.searchbusanshopapi.infra.exception.FailAuthenticationException;
import com.example.searchbusanshopapi.infra.exception.InvalidTokenException;
import com.example.searchbusanshopapi.infra.jwt.JwtService;
import com.example.searchbusanshopapi.infra.redis.RedisService;
import com.example.searchbusanshopapi.user.model.RefreshToken;
import com.example.searchbusanshopapi.user.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Transactional
@RestController
@RequiredArgsConstructor
public class LoginExceptionController {

    private final static String REFRESH_TOKEN_HEADER = "refresh";
    private final static String ACCESS_TOKEN_HEADER = "Authorization";

    private final RefreshRepository refreshRepository;
    private final RedisService redisService;
    private final JwtService jwtService;

    @GetMapping("/jwt/authentication")
    public void noAuthentication(@RequestParam String username){
        throw new FailAuthenticationException(Errorcode.NO_MATCHING_AUTHENTICATION_IN_DB);
    }

    @Transactional(rollbackOn = Exception.class)
    @GetMapping("/jwt/authentication/refresh")
    public ResponseEntity refreshBecauseExpired(HttpServletRequest request, HttpServletResponse response){

        String[] verifyResult = null;

        try {
            //[0] = 리프래시토큰내에 UUID, [1] = 토큰을 발급받은 유저네임
            verifyResult = jwtService.verifyRefreshToken(request.getHeader(REFRESH_TOKEN_HEADER));
        }catch (TokenExpiredException e){
            refreshRepository.deleteByUsername(verifyResult[1]);
            throw new InvalidTokenException(Errorcode.INVALID_TOKEN,
                    "새로 로그인해주세요.",
                    request.getHeader(REFRESH_TOKEN_HEADER));
        }
        RefreshToken refreshTokenDB = refreshRepository.findByUsername(verifyResult[1]);
        String[] verifyResultDB = jwtService.verifyRefreshToken(refreshTokenDB.getTokenValue());
        if(!verifyResult[0].equals(verifyResultDB[0])) {
            refreshRepository.deleteByUsername(verifyResult[1]);
            throw new InvalidTokenException(Errorcode.INVALID_TOKEN,
                    "새로 로그인해주세요.",
                    refreshTokenDB.getTokenValue());
        }
        String newAccessToken = jwtService.createToken(verifyResult[1]);
        return ResponseEntity.status(HttpStatus.CREATED).body("Bearer "+newAccessToken);

    }

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {
        String AccessToken = request.getHeader(ACCESS_TOKEN_HEADER);
        String username = jwtService.verifyToken(AccessToken);

        redisService.insertAccessToken(AccessToken, username);
        refreshRepository.deleteByUsername(username);
        SecurityContextHolder.clearContext();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
