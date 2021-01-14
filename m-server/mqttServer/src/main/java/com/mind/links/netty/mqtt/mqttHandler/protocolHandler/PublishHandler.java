package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import com.mind.links.netty.mqtt.common.ChannelCommon;
import com.mind.links.netty.mqtt.mqttStore.MqttSession;
import com.mind.links.netty.mqtt.mqttStore.message.RetainMessageStore;
import com.mind.links.netty.mqtt.mqttStore.publish.DupPublishMessageStore;
import com.mind.links.netty.mqtt.mqttStore.session.SessionStoreHandler;
import com.mind.links.netty.mqtt.mqttStore.subscribe.SubscribeStore;
import com.mind.links.netty.mqtt.mqttStore.subscribe.SubscribeStoreService;
import entity.InternalMessage;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * date: 2021-01-13 15:38
 * description
 *
 * @author qiDing
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PublishHandler implements IMqttMessageHandler {

    private final SessionStoreHandler sessionStoreHandler;

    private final SubscribeStoreService subscribeStoreService;


    @Override
    public Mono<Channel> publish(final Channel channel, final MqttMessage mqttMessage) {
        return Mono.just(channel)
                .doOnNext(c -> {
                    MqttPublishMessage msg = (MqttPublishMessage) mqttMessage;
                    String name = msg.variableHeader().topicName();
                    byte[] messageBytes = new byte[msg.payload().readableBytes()];
                    msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
                    String message = new String(messageBytes);
                    log.info("topic:" + name + ",msg:" + message);
                    this.processPublish(channel,msg);
                });
    }

    public void processPublish(Channel channel, MqttPublishMessage msg) {
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        // publish 延长session失效时间
        sessionStoreHandler.extendSession(channel);

        // QoS=0
        if (msg.fixedHeader().qosLevel() == MqttQoS.AT_MOST_ONCE) {
            byte[] messageBytes = new byte[msg.payload().readableBytes()];
            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
            InternalMessage internalMessage = new InternalMessage().setTopic(msg.variableHeader().topicName())
                    .setMqttQoS(msg.fixedHeader().qosLevel().value()).setMessageBytes(messageBytes)
                    .setDup(false).setRetain(false).setClientId(clientId);
//            internalCommunication.internalSend(internalMessage);
            this.sendPublishMessage(msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), messageBytes, false, false);
        }
        // QoS=1
        if (msg.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE) {
            byte[] messageBytes = new byte[msg.payload().readableBytes()];
            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
            InternalMessage internalMessage = new InternalMessage().setTopic(msg.variableHeader().topicName())
                    .setMqttQoS(msg.fixedHeader().qosLevel().value()).setMessageBytes(messageBytes)
                    .setDup(false).setRetain(false).setClientId(clientId);
            this.sendPublishMessage(msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), messageBytes, false, false);
            this.sendPubAckMessage(channel, msg.variableHeader().packetId());
        }
        // QoS=2
        if (msg.fixedHeader().qosLevel() == MqttQoS.EXACTLY_ONCE) {
            byte[] messageBytes = new byte[msg.payload().readableBytes()];
            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
            InternalMessage internalMessage = new InternalMessage().setTopic(msg.variableHeader().topicName())
                    .setMqttQoS(msg.fixedHeader().qosLevel().value()).setMessageBytes(messageBytes)
                    .setDup(false).setRetain(false).setClientId(clientId);
            this.sendPublishMessage(msg.variableHeader().topicName(), msg.fixedHeader().qosLevel(), messageBytes, false, false);
            this.sendPubRecMessage(channel, msg.variableHeader().packetId());
        }
        // retain=1, 保留消息
        if (msg.fixedHeader().isRetain()) {
            byte[] messageBytes = new byte[msg.payload().readableBytes()];
            msg.payload().getBytes(msg.payload().readerIndex(), messageBytes);
            if (messageBytes.length == 0) {

            }
        }
    }

    static int i = 0;

    public synchronized int getId() {
            return i++;
     }

    private void sendPublishMessage(String topic, MqttQoS mqttQoS, byte[] messageBytes, boolean retain, boolean dup) {
        List<SubscribeStore> subscribeStores = subscribeStoreService.search(topic);
        subscribeStores.forEach(subscribeStore -> {
            if (sessionStoreHandler.containsKey(subscribeStore.getClientId())) {
                // 订阅者收到MQTT消息的QoS级别, 最终取决于发布消息的QoS和主题订阅的QoS
                MqttQoS respQoS = mqttQoS.value() > subscribeStore.getMqttQoS() ? MqttQoS.valueOf(subscribeStore.getMqttQoS()) : mqttQoS;
                if (respQoS == MqttQoS.AT_MOST_ONCE) {
                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                            new MqttPublishVariableHeader(topic, 0), Unpooled.buffer().writeBytes(messageBytes));
                    log.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}", subscribeStore.getClientId(), topic, respQoS.value());
                    MqttSession sessionStore = sessionStoreHandler.get(subscribeStore.getClientId());
                    ChannelId channelId = ChannelCommon.channelIdMap.get(sessionStore.getBrokerId() + "_" + sessionStore.getChannelId());
                    if (channelId != null) {
                        Channel channel = ChannelCommon.channelGroup.find(channelId);
                        if (channel != null) channel.writeAndFlush(publishMessage);
                    }
                }
                if (respQoS == MqttQoS.AT_LEAST_ONCE) {
                    int messageId = i++;
                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                            new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
                    log.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", subscribeStore.getClientId(), topic, respQoS.value(), messageId);
                    DupPublishMessageStore dupPublishMessageStore = new DupPublishMessageStore().setClientId(subscribeStore.getClientId())
                            .setTopic(topic).setMqttQoS(respQoS.value()).setMessageBytes(messageBytes).setMessageId(messageId);
//                    dupPublishMessageStoreService.put(subscribeStore.getClientId(), dupPublishMessageStore);
                    MqttSession sessionStore = sessionStoreHandler.get(subscribeStore.getClientId());
                    ChannelId channelId = ChannelCommon.channelIdMap.get(sessionStore.getBrokerId() + "_" + sessionStore.getChannelId());
                    if (channelId != null) {
                        Channel channel = ChannelCommon.channelGroup.find(channelId);
                        if (channel != null) channel.writeAndFlush(publishMessage);
                    }
                }
                if (respQoS == MqttQoS.EXACTLY_ONCE) {
                    int messageId = getId();
                    MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, respQoS, retain, 0),
                            new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));
                    log.debug("PUBLISH - clientId: {}, topic: {}, Qos: {}, messageId: {}", subscribeStore.getClientId(), topic, respQoS.value(), messageId);
                    DupPublishMessageStore dupPublishMessageStore = new DupPublishMessageStore().setClientId(subscribeStore.getClientId())
                            .setTopic(topic).setMqttQoS(respQoS.value()).setMessageBytes(messageBytes).setMessageId(messageId);
//                    dupPublishMessageStoreService.put(subscribeStore.getClientId(), dupPublishMessageStore);
                    MqttSession sessionStore = sessionStoreHandler.get(subscribeStore.getClientId());
                    ChannelId channelId = ChannelCommon.channelIdMap.get(sessionStore.getBrokerId() + "_" + sessionStore.getChannelId());
                    if (channelId != null) {
                        Channel channel = ChannelCommon.channelGroup.find(channelId);
                        if (channel != null) channel.writeAndFlush(publishMessage);
                    }
                }
            }
        });
    }

    private void sendPubAckMessage(Channel channel, int messageId) {
        MqttPubAckMessage pubAckMessage = (MqttPubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
        channel.writeAndFlush(pubAckMessage);
    }

    private void sendPubRecMessage(Channel channel, int messageId) {
        MqttMessage pubRecMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
        channel.writeAndFlush(pubRecMessage);
    }
}
