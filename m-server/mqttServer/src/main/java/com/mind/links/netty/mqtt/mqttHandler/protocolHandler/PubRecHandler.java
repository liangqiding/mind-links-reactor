package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import com.mind.links.netty.mqtt.common.ChannelCommon;
import com.mind.links.netty.mqtt.mqttStore.publish.DupPubRelMessageStore;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
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
        log.debug("发布已接受（qos2 第一步）" + ChannelCommon.getClientId(channel));
        this.processPubRec(channel,(MqttMessageIdVariableHeader)msg.variableHeader());
        return Mono.just(channel);
    }

    public void processPubRec(Channel channel, MqttMessageIdVariableHeader variableHeader) {
        MqttMessage pubRelMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(variableHeader.messageId()), null);
        DupPubRelMessageStore dupPubRelMessageStore = new DupPubRelMessageStore().setClientId((String) channel.attr(AttributeKey.valueOf("clientId")).get())
                .setMessageId(variableHeader.messageId());
      //   dupPublishMessageStoreService.remove((String) channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
      // 存储消息    dupPubRelMessageStoreService.put((String) channel.attr(AttributeKey.valueOf("clientId")).get(), dupPubRelMessageStore);
        channel.writeAndFlush(pubRelMessage);
    }
}
