package com.mind.links.netty.mqtt.kafka;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author qiDing
 * date: 2021-01-03 14:32
 * @version v1.0.0
 * description
 */
@Component
@AllArgsConstructor
public class KafkaProducers {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * @author ：qiDing
     * @date ：Created in 2021/01/03 14:33
     * description：TODO kafka 提供者
     */
    public void send(String topic, Object o) {
        Mono.fromCallable(() -> kafkaTemplate.send(topic, o)).subscribe();
    }
}
