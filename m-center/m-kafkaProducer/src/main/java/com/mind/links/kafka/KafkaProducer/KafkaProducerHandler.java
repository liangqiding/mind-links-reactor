package com.mind.links.kafka.KafkaProducer;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * @author qiding
 */
@Component
@AllArgsConstructor
@Slf4j
public class KafkaProducerHandler {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public Mono<String> send(String data, String topic,Integer count) {
        return Flux.range(0, count)
                .map(integer -> kafkaTemplate.send(topic, data))
                .next()
                .flatMap(sendResultListenableFuture -> Mono.just("发送成功"));
    }

    @Async
    public void testSend(JSONObject jsonObject, String topic) {
            kafkaTemplate.send(topic, jsonObject.toJSONString());
    }

}
