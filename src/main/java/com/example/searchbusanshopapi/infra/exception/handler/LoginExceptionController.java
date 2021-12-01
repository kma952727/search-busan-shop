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

    /**
     * '/login'으로 요청시 디비에 일치한값이 없을경우
     * JwtAuthenticationFilter.class의 unsuccessfulAuthentication()메서드에서
     * 맵핑된 url로 리다이렉트하게 설정하였습니다.
     */
    @GetMapping("/jwt/authentication")
    public void noAuthentication(){
        throw new FailAuthenticationException(Errorcode.NO_MATCHING_AUTHENTICATION_IN_DB);
    }

    /**
     * accessToken이 만료되었을시 클라이언트에서 호출합니다.
     * 리퀘스트에 포함된 토큰과 db의 토큰이 만료가 안되었고,
     * 두토큰이 가지고있는 UUID가 동일할시 새로운 accessToken을 발급합니다.
     * @param request 리프레쉬토큰을 가지고옵니다.
     * @return 액세스토큰 발급
     */
    @Transactional(rollbackOn = Exception.class)
    @PostMapping("/jwt/authentication/refresh")
    public ResponseEntity refreshBecauseExpired(HttpServletRequest request){

        //[0] = 리프래시토큰내에 UUID, [1] = 토큰을 발급받은 유저네임
        String[] verifyResult = null;

        try {
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

    /**
     * 로그아웃 기능, 해당 엔드포인트 호출시 임베디드db에 요청에 포함된
     * access토큰을 넣고, refreshToken을 db에서 지웁니다.
     * @param request 해당 파라미터에서 엑세스토큰을 가져옵니다.
     * @return
     */
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
