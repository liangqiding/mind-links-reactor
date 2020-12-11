package com.mind.links.service;

import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


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

    public void saveObject() {
        RBucket<Object> bucket = redissonClient.getBucket("redisson:test");
        RMap<String, Object> map = redissonClient.getMap("redisson:map");
        bucket.set(666666666L);
        map.put("1", "hello");
        map.put("2", "hello2");
        map.put("3", "hello3");
        System.out.println(bucket.get());
        RMap<Object, Object> map2 = redissonClient.getMap("redisson:map");
        System.out.println(map2.get("2"));
        map2.remove("2");
        long l = redissonClient.getKeys().countExists("redisson:test");
        System.out.println("redisson:test===key sum=" + l);
        long l2 = redissonClient.getKeys().countExists("redisson:map");
        System.out.println("redisson:test===key sum=" + l2);
//        redissonClient.getKeys().flushall();
    }
}
