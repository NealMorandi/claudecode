package com.dom.dormitory.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis ユーティリティクラス
 */
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    /** 文字列を保存（有効期限なし） */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /** 文字列を保存（有効期限あり） */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /** 文字列を取得 */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /** キーを削除 */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /** キーの存在確認 */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /** 有効期限を設定 */
    public void expire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }
}
