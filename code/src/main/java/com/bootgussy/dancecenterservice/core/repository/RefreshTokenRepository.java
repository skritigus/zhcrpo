package com.bootgussy.dancecenterservice.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RefreshTokenRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_PREFIX = "refresh_token:";

    @Autowired
    public RefreshTokenRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(Long userId, String token, long durationDays) {
        redisTemplate.opsForValue().set(
                KEY_PREFIX + token,
                userId.toString(),
                durationDays,
                TimeUnit.DAYS
        );
    }

    public String findUserIdByToken(String token) {
        return (String) redisTemplate.opsForValue().get(KEY_PREFIX + token);
    }

    public void deleteToken(String token) {
        redisTemplate.delete(KEY_PREFIX + token);
    }
}
