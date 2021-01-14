package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * date: 2021-01-14 16:02
 * description 发布完成（qos2 第三步)
 *
 * @author qiDing
 */
@Component
@Slf4j
public class PubCompHandler implements IMqttMessageHandler{
    @Override
    public Mono<Channel> pubComp(Channel channel, MqttMessage msg) {
        log.debug("发布完成（qos2 第三步)"+channel);
        return Mono.just(channel);
    }
}
