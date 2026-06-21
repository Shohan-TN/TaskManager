package com.testproject.taskmanager.common.security;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {
    private final RedisTemplate<String, String> redisTemplate;

    public TokenBlacklistService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklist(String token, long ttlMillis) {
        String key = "blacklist:" + token;
        redisTemplate.opsForValue().set(key,token, ttlMillis, TimeUnit.MILLISECONDS);
    }

    public boolean inBlacklist(String token) {
        String key = "blacklist:" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
