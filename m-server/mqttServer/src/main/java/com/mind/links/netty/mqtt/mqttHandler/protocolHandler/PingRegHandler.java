package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import com.mind.links.netty.mqtt.mqttStore.MqttSession;
import com.mind.links.netty.mqtt.config.BrokerProperties;
import com.mind.links.netty.mqtt.mqttServer.MqttBrokerServer;
import com.mind.links.netty.mqtt.mqttStore.session.SessionStoreHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * date: 2021-01-13 10:12
 * description PING_REQ ping请求事件处理,
 *
 * @author qiDing
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PingRegHandler implements IMqttMessageHandler {

    private final SessionStoreHandler sessionStoreHandler;

    private final BrokerProperties brokerProperties;


    @Override
    public Mono<Void> pingReq(final ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        if (sessionStoreHandler.containsKey(clientId)) {
            MqttSession sessionStore = sessionStoreHandler.get(clientId);
            ChannelId channelId = MqttBrokerServer.channelIdMap.get(sessionStore.getBrokerId() + "_" + sessionStore.getChannelId());
            if (brokerProperties.getId().equals(sessionStore.getBrokerId()) && channelId != null) {
                sessionStoreHandler.expire(clientId, sessionStore.getExpire());
                MqttMessage pingRespMessage = MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0), null, null);
                log.debug("PING_REQ - clientId: {}", clientId);
                channel.writeAndFlush(pingRespMessage);
            }
        }
        return Mono.empty();
    }

}
