package com.example.searchbusanshopapi.infra.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${external.jwt.secret-key}")
    private String SECRET_KEK;
    //토큰 생성
    public String createToken(String username){
        String jwtToken = JWT.create()
                .withSubject("required-token")
                .withExpiresAt(new Date(System.currentTimeMillis() + 60000 * 10))
                .withClaim("username", username)
                .sign(Algorithm.HMAC256(SECRET_KEK));
        return jwtToken;
    }

    public String createRefreshToken(String username, String UUIDValue){
        String jwtToken = JWT.create()
                .withSubject("required-token")
                .withExpiresAt(new Date(System.currentTimeMillis() + 60000 * 60 * 12))
                .withClaim("username", username)
                .withClaim("UUID", UUIDValue)
                .sign(Algorithm.HMAC256(SECRET_KEK));
        return jwtToken;
    }

    //토큰검증
    public String verifyToken(String token) {
        DecodedJWT decodedJWT = getDecodedToken(token);

        String username = decodedJWT.getClaim("username").asString();
        return username;
    }
    //리프레시 토큰검증
    public String[] verifyRefreshToken(String token) {
        DecodedJWT decodedJWT = getDecodedToken(token);

        String tokenValue = decodedJWT.getClaim("UUID").asString();
        String usernanme = decodedJWT.getClaim("username").asString();
        return new String[]{tokenValue, usernanme};
    }
    private DecodedJWT getDecodedToken(String token){
        token = token.replaceAll("Bearer ", "");
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SECRET_KEK))
                .build()
                .verify(token);
        return decodedJWT;
    }
}
