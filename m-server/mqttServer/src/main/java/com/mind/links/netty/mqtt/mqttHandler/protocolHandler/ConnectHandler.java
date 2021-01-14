package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import com.mind.links.netty.mqtt.mqttFilter.MqttFilter;
import com.mind.links.netty.mqtt.mqttStore.publish.DupPubRelMessageStore;
import com.mind.links.netty.mqtt.mqttStore.publish.DupPublishMessageStore;
import com.mind.links.netty.mqtt.config.BrokerProperties;
import com.mind.links.netty.mqtt.mqttStore.MqttSession;
import com.mind.links.netty.mqtt.mqttStore.session.SessionStoreHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.List;


/**
 * CONNECT连接报文处理
 *
 * @author qiding
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ConnectHandler implements IMqttMessageHandler {

    private final SessionStoreHandler sessionStoreHandler;

    private final BrokerProperties brokerProperties;

    private final MqttFilter mqttFilter;


    @Override
    public Mono<Channel> connect(Channel channel, final MqttMessage mqttMessage) {
        MqttConnectMessage msg = (MqttConnectMessage) mqttMessage;
        return Mono.just(channel)
                .flatMap(c -> mqttFilter.filter(c, msg))
                .flatMap(c -> this.saveSession(c, msg))
                .flatMap(c -> this.response(c, msg))
                .doOnNext(c -> this.sendUndoneMessage(c, msg));
    }


    /**
     * 保存session
     */
    public Mono<Channel> saveSession(final Channel channel, MqttConnectMessage msg) {
        int expire = this.refreshIdle(channel, msg);
        MqttSession mqttSession = this.getMqttSession(channel, msg, expire);
        return Mono.just(channel)
                .doOnNext(c -> sessionStoreHandler.put(msg.payload().clientIdentifier(), mqttSession, expire));
    }

    /**
     * 刷新session有效时间
     */
    public int refreshIdle(Channel channel, MqttConnectMessage msg) {
        int expire = 0;
        if (msg.variableHeader().keepAliveTimeSeconds() > 0) {
            if (channel.pipeline().names().contains("idle")) {
                channel.pipeline().remove("idle");
            }
            expire = (Math.round(msg.variableHeader().keepAliveTimeSeconds() * 1.5f));
            channel.pipeline().addFirst("idle", new IdleStateHandler(0, 0, expire));
        }
        log.debug("到期时间expire:" + expire);
        return expire;
    }

    /**
     * 封装session
     */
    public MqttSession getMqttSession(Channel channel, MqttConnectMessage msg, int expire) {
        MqttSession mqttSession = new MqttSession()
                .setBrokerId(brokerProperties.getId())
                .setClientId(msg.payload().clientIdentifier())
                .setChannelId(channel.id().asLongText())
                .setCleanSession(msg.variableHeader().isCleanSession())
                .setExpire(expire);
        // 是否设置遗嘱
        if (msg.variableHeader().isWillFlag()) {
            MqttPublishMessage willMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.valueOf(msg.variableHeader().willQos()), msg.variableHeader().isWillRetain(), 0),
                    new MqttPublishVariableHeader(msg.payload().willTopic(), 0), Unpooled.buffer().writeBytes(msg.payload().willMessageInBytes()));
            mqttSession.setWillMessage(willMessage);
        }
        return mqttSession;
    }

    public Mono<Channel> response(final Channel channel, MqttConnectMessage msg) {
        return Mono.just(channel)
                .doOnNext(c -> {
                    c.attr(AttributeKey.valueOf("clientId")).set(msg.payload().clientIdentifier());
                    boolean sessionPresent = sessionStoreHandler.containsKey(msg.payload().clientIdentifier()) && !msg.variableHeader().isCleanSession();
                    MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                            new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, sessionPresent), null);
                    c.writeAndFlush(okResp);
                    log.debug("===3 CONNECT - clientId: {}, cleanSession: {}", msg.payload().clientIdentifier(), msg.variableHeader().isCleanSession());
                });
    }

    /**
     * 如果cleanSession为0, 客户端CleanSession=0时，上线接收离线消息，源码分析,需要重发同一clientId存储的未完成的QoS1和QoS2的DUP消息
     */
    public void sendUndoneMessage(final Channel channel, MqttConnectMessage msg) {
        log.debug("isCleanSession:" + msg.variableHeader().isCleanSession());
        if (!msg.variableHeader().isCleanSession()) {
            List<DupPublishMessageStore> dupPublishMessageStoreList = null;
            List<DupPubRelMessageStore> dupPubRelMessageStoreList = null;
            dupPublishMessageStoreList.forEach(dupPublishMessageStore -> {
                MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.PUBLISH, true, MqttQoS.valueOf(dupPublishMessageStore.getMqttQoS()), false, 0),
                        new MqttPublishVariableHeader(dupPublishMessageStore.getTopic(), dupPublishMessageStore.getMessageId()), Unpooled.buffer().writeBytes(dupPublishMessageStore.getMessageBytes()));
                channel.writeAndFlush(publishMessage);
            });
            dupPubRelMessageStoreList.forEach(dupPubRelMessageStore -> {
                MqttMessage pubRelMessage = MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.PUBREL, true, MqttQoS.AT_MOST_ONCE, false, 0),
                        MqttMessageIdVariableHeader.from(dupPubRelMessageStore.getMessageId()), null);
                channel.writeAndFlush(pubRelMessage);
            });
        }
    }

}
