package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import com.mind.links.netty.mqtt.common.MqttSession;
import com.mind.links.netty.mqtt.config.BrokerProperties;
import com.mind.links.netty.mqtt.mqttServer.MqttBrokerServer;
import com.mind.links.netty.mqtt.mqttStore.session.SessionStoreHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * date: 2021-01-13 10:12
 * description PING_REQ ping请求事件处理,
 *
 * @author qiDing
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PingRegHandler {

    private final SessionStoreHandler sessionStoreHandler;

    private final BrokerProperties brokerProperties;

    public void processPingReq(Channel channel) {
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        if (sessionStoreHandler.containsKey(clientId)) {
            MqttSession sessionStore = sessionStoreHandler.get(clientId);
            ChannelId channelId = MqttBrokerServer.channelIdMap.get(sessionStore.getBrokerId() + "_" + sessionStore.getChannelId());
            if (brokerProperties.getId().equals(sessionStore.getBrokerId()) && channelId != null) {
                sessionStoreHandler.expire(clientId, sessionStore.getExpire());
                MqttMessage pingRespMessage = MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0), null, null);
                log.debug("PINGREQ - clientId: {}", clientId);
                channel.writeAndFlush(pingRespMessage);
            }
        }
    }
}
