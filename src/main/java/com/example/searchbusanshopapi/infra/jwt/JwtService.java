package com.example.searchbusanshopapi.infra.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

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

    //토큰검증
    public String verifyToken(String token) {
        token = token.replaceAll("Bearer ", "");
        String username = JWT.require(Algorithm.HMAC256(SECRET_KEK))
                .build()
                .verify(token)
                .getClaim("username").asString();
        return username;

    }
}
