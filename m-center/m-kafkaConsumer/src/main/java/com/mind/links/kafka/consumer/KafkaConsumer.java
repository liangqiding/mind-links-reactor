package com.mind.links.kafka.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * DateTime: 2020/6/16 0016 14:15
 * Description: TODO
 * @author qiding
 */
@Component
@AllArgsConstructor
@Slf4j
public class KafkaConsumer {
    @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.group}")
    public void data(ConsumerRecord<?,?> consumerRecord) {
        Object value = consumerRecord.value();
        if (log.isInfoEnabled()) {
            log.info("offset {}, value {}", consumerRecord.offset(), consumerRecord.value());
        }
        if (null == value) {
            log.error("kafka消费数据为空");
        }
        log.info((String) value);
    }

    @KafkaListener(topics = "logger", groupId = "${kafka.group}")
    public void logger(ConsumerRecord<?,?> consumerRecord) {
        Object value = consumerRecord.value();
        log.info((String) value);
    }

}
