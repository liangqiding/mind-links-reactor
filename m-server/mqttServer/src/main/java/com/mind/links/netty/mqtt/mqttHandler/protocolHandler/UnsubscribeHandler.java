package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import com.mind.links.netty.mqtt.mqttStore.subscribe.SubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * date: 2021-01-14 16:07
 * description 取消订阅
 *
 * @author qiDing
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UnsubscribeHandler implements IMqttMessageHandler{

    private final SubscribeStoreService subscribeStoreService;

    @Override
    public Mono<Channel> unsubscribe(Channel channel, MqttMessage msg) {
        log.debug("取消订阅:"+channel);
        this.processUnSubscribe(channel,(MqttUnsubscribeMessage)msg);
        return Mono.just(channel);
    }

    public void processUnSubscribe(Channel channel, MqttUnsubscribeMessage msg) {
        List<String> topicFilters = msg.payload().topics();
        String clinetId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        topicFilters.forEach(topicFilter -> {
            subscribeStoreService.remove(topicFilter, clinetId);
            log.debug("UNSUBSCRIBE - clientId: {}, topicFilter: {}", clinetId, topicFilter);
        });
        MqttUnsubAckMessage unsubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()), null);
        channel.writeAndFlush(unsubAckMessage);
    }
}
