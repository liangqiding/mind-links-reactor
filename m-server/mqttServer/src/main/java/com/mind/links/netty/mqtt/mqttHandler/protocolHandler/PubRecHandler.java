package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * date: 2021-01-14 15:55
 * description 发布已接受（qos2 第一步）
 *
 * @author qiDing
 */
@Component
@Slf4j
public class PubRecHandler implements IMqttMessageHandler {
    @Override
    public Mono<Channel> pubRec(Channel channel, MqttMessage msg) {
        log.debug("发布已接受（qos2 第一步）" + channel);
        return Mono.just(channel);
    }
}
