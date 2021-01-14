package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * date: 2021-01-14 16:04
 * description 订阅确认
 *
 * @author qiDing
 */
@Component
@Slf4j
public class SubAckHandler implements IMqttMessageHandler{
    @Override
    public Mono<Channel> subAck(Channel channel, MqttMessage msg) {
        log.debug("订阅确认:"+channel);
        return Mono.just(channel);
    }
}
