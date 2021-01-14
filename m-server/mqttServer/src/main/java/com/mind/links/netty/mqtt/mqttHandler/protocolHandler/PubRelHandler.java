package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * date: 2021-01-14 16:00
 * description 发布已释放（qos2 第二步）
 *
 * @author qiDing
 */
@Component
@Slf4j
public class PubRelHandler implements IMqttMessageHandler{
    @Override
    public Mono<Channel> pubRel(Channel channel, MqttMessage msg) {
        log.debug("发布已释放（qos2 第二步）"+channel);
        this.processPubRel(channel,(MqttMessageIdVariableHeader)msg.variableHeader());
        return Mono.just(channel);
    }

    public void processPubRel(Channel channel, MqttMessageIdVariableHeader variableHeader) {
        MqttMessage pubCompMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(variableHeader.messageId()), null);
        log.debug("PUBREL - clientId: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
        channel.writeAndFlush(pubCompMessage);
    }
}
