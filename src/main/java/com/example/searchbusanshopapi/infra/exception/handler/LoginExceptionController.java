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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.CascadeType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

/**
 * 로그인실패, noAuthentication()
 * accessToken재발급, refreshBecauseExpired()
 * 로그아웃, logout()
 * 기능을 처리하는 클래스입니다.
 *
 */
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
     * + redis에 기존에 있던 accessToken을 올립니다.
     * @param request 리프레쉬토큰, 엑세스토큰을 가지고옵니다.
     * @return 액세스토큰 발급
     */
    @Transactional(rollbackOn = Exception.class)
    @PostMapping("/jwt/authentication/refresh")
    public ResponseEntity refreshBecauseExpired(HttpServletRequest request){

        //[0] = 리프래시토큰내에 UUID, [1] = 토큰을 발급받은 유저네임
        String[] verifyResult = null;
        String expiredAccessToken = request.getHeader(ACCESS_TOKEN_HEADER);
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER);

        //제공받은 리프레시토큰이 유효한지 검증
        try {
            verifyResult = jwtService.verifyRefreshToken(refreshToken);
        }catch (TokenExpiredException e){
            refreshRepository.deleteByUsername(verifyResult[1]);
            throw new InvalidTokenException(Errorcode.INVALID_TOKEN, "새로 로그인해주세요.", refreshToken);
        }

        //db에 저장된 리프레시토큰이 유효한지검사후, 제공받은 리프레시토큰과 일치한지 검증
        RefreshToken refreshTokenDB = refreshRepository.findByUsername(verifyResult[1]);
        String[] verifyResultDB = jwtService.verifyRefreshToken(refreshTokenDB.getTokenValue());
        if(!verifyResult[0].equals(verifyResultDB[0])) {
            refreshRepository.deleteByUsername(verifyResult[1]);
            throw new InvalidTokenException(Errorcode.INVALID_TOKEN,
                    "새로 로그인해주세요.",
                    refreshTokenDB.getTokenValue());
        }
        //모두 통과되면 redis에 전에 사용하던 accessToken을 저장(이후 요청이 있을경우 포함된 토큰값을 redis의 데이터와 비교, 있다면 요청차단)
        //이후 새로운 엑세스토큰을 발급
        redisService.insertAccessToken(expiredAccessToken);
        String newAccessToken = jwtService.createToken(verifyResult[1]);
        return ResponseEntity.status(HttpStatus.CREATED).body("Bearer "+newAccessToken);

    }

    /**
     * 로그아웃 기능, 해당 엔드포인트 호출시 임베디드db에 요청에 포함된
     * access토큰을 넣고, refreshToken을 db에서 지웁니다.
     * @param request 해당 파라미터에서 엑세스토큰을 가져옵니다.
     * @return 없음
     */
    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {
        String AccessToken = request.getHeader(ACCESS_TOKEN_HEADER);
        String username = jwtService.verifyToken(AccessToken);
        redisService.insertAccessToken(AccessToken);
        refreshRepository.deleteByUsername(username);
        SecurityContextHolder.clearContext();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/denied")
    public void accessDenied(@RequestParam String cause){
        throw new AccessDeniedException(cause + "의 권한으로는 접근할수 없습니다.");
    }
}
