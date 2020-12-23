package com.mind.links.kafka.controller;

import com.alibaba.fastjson.JSONObject;
import com.mind.links.kafka.KafkaProducer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Author: QiDing
 * DateTime: 2020/6/16 0016 14:09
 * Description: TODO
 */
@RestController
public class Index {
    @Autowired
    KafkaProducer kafkaProducer;
    private final Logger logger = LoggerFactory.getLogger(Index.class);

    @RequestMapping("/send")
    public String test(String msg, String topic, Integer count) {
        kafkaProducer.send(msg, topic, count);
        return "success";
    }


    /**
     * {
     * "payload":"[{"data":1,"function_tag":"SD103Emg"}]",
     * "topic":"i2dsp/Emg/0/SD103/MSTest000001LTK000000040001/thing/event/SD103Emg/post"
     * }
     */
    @RequestMapping("/testSend")
    public String testSend(@RequestBody  JSONObject payload, String topic, @RequestParam(defaultValue = "1") Integer count) {
        logger.info("================================================================================");
        logger.info("===" + payload);
        for (int i = 0; i < count; i++) {
            kafkaProducer.testSend(payload, topic);
        }
        return "success";
    }


}
