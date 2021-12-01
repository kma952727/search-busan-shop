package com.example.searchbusanshopapi.infra.redis;

import com.example.searchbusanshopapi.infra.config.RedisConfig;
import com.example.searchbusanshopapi.infra.exception.Errorcode;
import com.example.searchbusanshopapi.infra.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final static int TIME_OUT = 60;

    private final RedisConfig redisConfig;
    private RedisTemplate<String, String> template;
    private ValueOperations<String, String> vop;

    @PostConstruct
    public void init(){
        template = redisConfig.redisTemplate();
        vop = template.opsForValue();
    }

    public void insertAccessToken(String AccessToken, String username){

        vop.set(username, AccessToken);
        template.expire(username, TIME_OUT, TimeUnit.SECONDS);

    }

    public void isBlockToken(String accessToken, String username) {
        String blockToken = vop.get(username);
        if(blockToken == null) return;
        if(blockToken.equals(accessToken)){
            throw new InvalidTokenException(Errorcode.INVALID_TOKEN,
                    "",
                    blockToken);
        }
    }
}
