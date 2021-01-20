package com.mind.links.cache.redisson.service;


import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * description :
 *
 * @author : qiDing
 * date: 2020-12-10 14:45
 * @version v1.0.0
 */

@Service
@RequiredArgsConstructor
public class RedissonServiceImp {

    private final RedissonReactiveClient redissonReactiveClient;

    public Mono<Boolean> saveObject(String key, String mapKey, Object value) {
        return redissonReactiveClient.getMap(key).fastPut(mapKey, value);
    }
}
