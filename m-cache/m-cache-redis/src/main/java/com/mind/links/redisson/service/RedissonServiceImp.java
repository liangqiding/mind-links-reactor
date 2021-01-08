package com.mind.links.redisson.service;


import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import javax.annotation.Resource;
import java.util.Optional;


/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-10 14:45
 * @version v1.0.0
 */

@Service
public class RedissonServiceImp {
    @Resource
    RedissonClient redissonClient;
    @Resource
    RedissonReactiveClient redissonReactiveClient;

    public Mono<Boolean> saveObject(String key, String mapKey, Object value) {
        return Mono.just(Optional.ofNullable(value)).map(v -> {
            RMap<String, Object> map = redissonClient.getMap(key);
            map.put(mapKey, v);
            return true;
        });
    }
}
