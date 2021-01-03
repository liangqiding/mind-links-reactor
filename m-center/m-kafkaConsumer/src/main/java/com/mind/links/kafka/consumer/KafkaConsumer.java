package com.mind.links.kafka.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * DateTime: 2020/6/16 0016 14:15
 * Description: TODO
 *
 * @author qiding
 */
@Component
@AllArgsConstructor
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.group}")
    public void data(List<ConsumerRecord<?, ?>> consumerRecords) {
        Flux.fromIterable(consumerRecords).subscribe(consumerRecord -> {
            if (log.isInfoEnabled()) {
                log.info("topic {} , offset {}, value {} ", consumerRecord.topic(), consumerRecord.offset(), consumerRecord.value());
            }
        });
    }

    @KafkaListener(topics = "logger", groupId = "${kafka.group}")
    public void logger(List<ConsumerRecord<?, ?>> consumerRecords) {
        Flux.fromIterable(consumerRecords).subscribe(consumerRecord -> {
            if (log.isInfoEnabled()) {
                log.info("topic {} , offset {}, value {} ", consumerRecord.topic(), consumerRecord.offset(), consumerRecord.value());
            }
        });
    }

    @KafkaListener(topics = "webSocket", groupId = "${kafka.group}")
    public void webSocket(List<ConsumerRecord<?, ?>> consumerRecords) {
        Flux.fromIterable(consumerRecords).subscribe(consumerRecord -> {
            if (log.isInfoEnabled()) {
                log.info("topic {} , offset {}, value {} ", consumerRecord.topic(), consumerRecord.offset(), consumerRecord.value());
            }
        });
    }

    @KafkaListener(topics = "socket", groupId = "${kafka.group}")
    public void socket(List<ConsumerRecord<?, ?>> consumerRecords) {
        Flux.fromIterable(consumerRecords).subscribe(consumerRecord -> {
            if (log.isInfoEnabled()) {
                log.info("topic {} , offset {}, value {} ", consumerRecord.topic(), consumerRecord.offset(), consumerRecord.value());
            }
        });
    }
}
