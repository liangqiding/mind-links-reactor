package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * date: 2021-01-13 15:38
 * description
 *
 * @author qiDing
 */
@Slf4j
@Component
public class PublishHandler implements IMqttMessageHandler {

    @Override
    public Mono<Void> publish(final ChannelHandlerContext ctx, final MqttMessage mqttMessage) {
        MqttPublishMessage msg = (MqttPublishMessage) mqttMessage;
        String name = msg.variableHeader().topicName();
        byte[] messageBytes = new byte[msg.payload().readableBytes()];
        msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
        String message = new String(messageBytes);
        log.info("topic:" + name + ",msg:" + message);
        return Mono.empty();
    }

}
