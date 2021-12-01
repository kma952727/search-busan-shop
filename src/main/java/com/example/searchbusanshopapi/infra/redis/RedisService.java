package com.example.searchbusanshopapi.infra.redis;

import com.example.searchbusanshopapi.infra.config.RedisConfig;
import com.example.searchbusanshopapi.infra.exception.Errorcode;
import com.example.searchbusanshopapi.infra.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisConfig redisConfig;
    private RedisTemplate<String, String> template;
    private SetOperations<String, String> sop;

    @PostConstruct
    public void init(){
        template = redisConfig.redisTemplate();
        sop = template.opsForSet();
    }

    public void insertAccessToken(String AccessToken){
        sop.add("tokenSet",AccessToken);
    }

    public void isBlockToken(String accessToken) {
        boolean blockToken = sop.isMember("tokenSet", accessToken);
        if(blockToken){
            throw new InvalidTokenException(Errorcode.INVALID_TOKEN,
                    "",
                    accessToken);
        }
    }

    public void removeToken(String jwtHeader) {
        sop.remove("tokenSet", jwtHeader);
    }
}
