package com.mind.links.cache.redisson;

import com.alibaba.fastjson.JSON;
import com.mind.links.cache.redisson.common.MqttSubscribe;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * date: 2021-01-16 08:31
 * description
 *
 * @author qiDing
 */
@SpringBootTest
public class Tests {
    @Autowired
    RedissonReactiveClient redissonReactiveClient;

    @Test
    void saveMap() throws InterruptedException {
        redissonReactiveClient.getAtomicLong("add").addAndGet(1).subscribe(System.out::println);
        redissonReactiveClient.getAtomicLong("add").addAndGet(1).subscribe(System.out::println);
        redissonReactiveClient.getAtomicLong("add").addAndGet(1).subscribe(System.out::println);
        redissonReactiveClient.getAtomicLong("add").addAndGet(1).subscribe(System.out::println);
        redissonReactiveClient.getAtomicLong("add").addAndGet(1).subscribe(System.out::println);
    }

    public static void main(String[] args) {
        Mono.just("66")
                .flatMap(s -> Mono.empty())
                .then(Mono.just("111"))
                .flatMap(o -> test("1"))
                .subscribe(System.out::println);
    }

    public static Mono<String> test(String s) {
        return Mono.just(s);
    }

    public static Mono<Boolean> test(int i) {
        return Mono.just(false).map(aBoolean -> {
            return true;
        });
    }
}
