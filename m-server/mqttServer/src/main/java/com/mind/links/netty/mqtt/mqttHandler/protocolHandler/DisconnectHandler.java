package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * date: 2021-01-14 16:15
 * description 断开连接
 *
 * @author qiDing
 */
@Component
@Slf4j
public class DisconnectHandler implements IMqttMessageHandler{

    @Override
    public Mono<Channel> disconnect(Channel channel) {
        log.debug("断开连接:"+channel);
        return Mono.just(channel);
    }
}
