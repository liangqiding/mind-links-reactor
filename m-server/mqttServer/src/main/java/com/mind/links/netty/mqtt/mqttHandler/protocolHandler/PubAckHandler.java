package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * date: 2021-01-14 15:52
 * description 发布确认
 *
 * @author qiDing
 */
@Component
@Slf4j
public class PubAckHandler implements IMqttMessageHandler{

    @Override
    public Mono<Channel> pubAck(Channel channel, MqttMessage msg) {
        log.debug("发布确认"+channel);
        return Mono.just(channel);
    }
}
