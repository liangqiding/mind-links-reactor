package com.mind.links.netty.kafka.KafkaProducer;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;


    public void send(String data, String topic, Integer count) {
        for (int i = 1; i <= count; i++) {
            data(data + i, topic);
        }
    }


    @Async
    public void data(String data, String topic) {
        try {
            kafkaTemplate.send(topic, "这是测试的数据==>" + data);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("出错！！！！！！！！！！！");
        }
    }

    @Async
    public void testSend(JSONObject jsonObject, String topic) {
        try {
            kafkaTemplate.send(topic, jsonObject.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("出错！！！！！！！！！！！");
        }
    }




}
