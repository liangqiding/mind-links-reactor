package com.mind.links.kafka.controller;

import com.mind.links.kafka.KafkaProducer.KafkaProducerHandler;
import com.mind.links.logger.handler.aopLog.CustomAopHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * DateTime: 2020/6/16 0016 14:09
 * Description: 消息提供测试
 *
 * @author qiding
 */
@RestController
@Slf4j
@ApiModel("kafka提供者测试api")
public class Index {

    @Autowired
    private KafkaProducerHandler kafkaProducerHandler;

    @GetMapping(value = "/send", produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    @ApiOperation("创建存储桶")
    @CustomAopHandler(module = "minio", desc = "创建存储桶")
    public Mono<String> send(String msg, String topic, Integer count) {
        return kafkaProducerHandler.send(msg, topic, count);
    }

}
