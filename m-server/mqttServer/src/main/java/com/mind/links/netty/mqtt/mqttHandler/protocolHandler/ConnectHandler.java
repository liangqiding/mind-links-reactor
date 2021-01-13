package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import com.mind.links.netty.mqtt.mqttFilter.MqttFilter;
import com.mind.links.netty.mqtt.mqttStore.publish.DupPubRelMessageStore;
import com.mind.links.netty.mqtt.mqttStore.publish.DupPublishMessageStore;
import com.mind.links.netty.mqtt.config.BrokerProperties;
import com.mind.links.netty.mqtt.common.MqttSession;
import com.mind.links.netty.mqtt.mqttStore.session.SessionStoreHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;


/**
 * CONNECT连接报文处理
 *
 * @author qiding
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ConnectHandler {

    private final SessionStoreHandler sessionStoreHandler;

    private final BrokerProperties brokerProperties;

    private final MqttFilter mqttFilter;

    public void connect(Channel channel, MqttConnectMessage msg) {

        mqttFilter.filter(channel, msg);

        int expire = this.idleHandler(channel, msg);

        this.saveSession(channel, msg, expire);

        this.response(channel, msg);

        this.sendUndoneMessage(channel, msg);
    }

    public int idleHandler(Channel channel, MqttConnectMessage msg) {
        // 处理连接心跳包
        int expire = 0;
        if (msg.variableHeader().keepAliveTimeSeconds() > 0) {
            if (channel.pipeline().names().contains("idle")) {
                channel.pipeline().remove("idle");
            }
            expire = Math.round(msg.variableHeader().keepAliveTimeSeconds() * 1.5f);
            channel.pipeline().addFirst("idle", new IdleStateHandler(0, 0, expire));
        }
        log.debug("===1expire:" + expire);
        return expire;
    }

    public void saveSession(Channel channel, MqttConnectMessage msg, int expire) {
        // 处理遗嘱信息 并生成session
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
        // 至此存储会话信息及返回接受客户端连接
        sessionStoreHandler.put(msg.payload().clientIdentifier(), mqttSession, expire);
        log.debug("===2mqttSession:" + mqttSession);
    }

    public void response(Channel channel, MqttConnectMessage msg) {
        // 将clientId存储到channel的map中
        channel.attr(AttributeKey.valueOf("clientId")).set(msg.payload().clientIdentifier());
        boolean sessionPresent = sessionStoreHandler.containsKey(msg.payload().clientIdentifier()) && !msg.variableHeader().isCleanSession();
        MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, sessionPresent), null);
        channel.writeAndFlush(okResp);
        log.debug("===3 CONNECT - clientId: {}, cleanSession: {}", msg.payload().clientIdentifier(), msg.variableHeader().isCleanSession());
    }

    public void sendUndoneMessage(Channel channel, MqttConnectMessage msg) {
        //  TODO: 2021/1/12   如果cleanSession为0, 客户端CleanSession=0时，上线接收离线消息，源码分析,需要重发同一clientId存储的未完成的QoS1和QoS2的DUP消息
        log.debug("isCleanSession:"+msg.variableHeader().isCleanSession());
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
