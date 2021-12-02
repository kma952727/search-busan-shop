package com.example.searchbusanshopapi.infra.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

/**
 * jwt에 관한 기능을 지원하는 클래스입니다.
 */
@Service
public class JwtService {

    @Value("${external.jwt.secret-key}")
    private String SECRET_KEK;

    //엑세스토큰 생성, 10분의 유효기간을 가집니다.
    public String createToken(String username){
        String jwtToken = JWT.create()
                .withSubject("required-token")
                .withExpiresAt(new Date(System.currentTimeMillis() + 60000 * 10))
                .withClaim("username", username)
                .sign(Algorithm.HMAC256(SECRET_KEK));
        return jwtToken;
    }

    //리프레시토큰을 생성합니다, 12시간의 유효기간을 가집니다.
    public String createRefreshToken(String username, String UUIDValue){
        String jwtToken = JWT.create()
                .withSubject("required-token")
                .withExpiresAt(new Date(System.currentTimeMillis() + 60000 * 60 * 12))
                .withClaim("username", username)
                .withClaim("UUID", UUIDValue)
                .sign(Algorithm.HMAC256(SECRET_KEK));
        return jwtToken;
    }

    //토큰을 검증한후 username을 반환합니다.
    public String verifyToken(String token) {
        DecodedJWT decodedJWT = getDecodedToken(token);

        String username = decodedJWT.getClaim("username").asString();
        return username;
    }

    //토큰을 검증한후 UUID, username을 반환합니다.(리프레시토큰용)
    public String[] verifyRefreshToken(String token) {
        DecodedJWT decodedJWT = getDecodedToken(token);

        String tokenValue = decodedJWT.getClaim("UUID").asString();
        String usernanme = decodedJWT.getClaim("username").asString();
        return new String[]{tokenValue, usernanme};
    }

    //verify**()메서드에서 호출합니다. 토큰의 유효성을 검사합니다.
    private DecodedJWT getDecodedToken(String token){
        token = token.replaceAll("Bearer ", "");
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SECRET_KEK))
                .build()
                .verify(token);
        return decodedJWT;
    }
}
