package com.hanghae.newsfeed.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public void setValues(String key, String value, Long expiration, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expiration, timeUnit);
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean keyExists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    public boolean checkExistsValue(String value) {
        return !value.equals("false");
    }
}