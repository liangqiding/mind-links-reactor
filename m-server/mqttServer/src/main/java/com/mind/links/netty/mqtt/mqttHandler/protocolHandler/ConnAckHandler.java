package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * date: 2021-01-14 15:44
 * description 确认连接请求
 *
 * @author qiDing
 */
@Slf4j
@Component
public class ConnAckHandler implements IMqttMessageHandler{

    @Override
    public Mono<Channel> connAck(Channel channel, MqttMessage msg) {
        log.debug("确认连接请求"+channel);
        return Mono.just(channel);
    }
}
