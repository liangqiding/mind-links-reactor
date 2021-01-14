package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.util.AttributeKey;
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
        this.processPubComp(channel,(MqttMessageIdVariableHeader)msg.variableHeader());
        return Mono.just(channel);
    }

    public void processPubComp(Channel channel, MqttMessageIdVariableHeader variableHeader) {
        int messageId = variableHeader.messageId();
        log.debug("PUBCOMP - clientId: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get(), messageId);
//    释放缓存    dupPubRelMessageStoreService.remove((String) channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
    }
}
