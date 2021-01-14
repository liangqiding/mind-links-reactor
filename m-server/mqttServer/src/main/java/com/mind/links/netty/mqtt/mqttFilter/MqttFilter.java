package com.mind.links.netty.mqtt.mqttFilter;

import cn.hutool.core.util.StrUtil;
import com.mind.links.netty.mqtt.config.BrokerProperties;
import com.mind.links.netty.mqtt.mqttStore.session.SessionStoreHandler;
import exception.LinksExceptionTcp;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * date: 2021-01-12 15:23
 * description  过滤器核心类
 *
 * @author qiDing
 */
@Component
@RequiredArgsConstructor
public class MqttFilter implements IMqttFilter {

    private final BrokerProperties brokerProperties;

    private final SessionStoreHandler sessionStoreHandler;

    @Override
    public Mono<Channel> filter(final Channel channel, final MqttConnectMessage msg) {
        return Mono.just(channel)
                .doFirst(() -> this.sessionStoreHandler.cleanSession(msg))
                .filter(c -> this.isDecoderSuccess(c, msg))
                .switchIfEmpty(LinksExceptionTcp.errors("解码器故障"))
                .filter(c -> this.clientIdIsNotNull(c, msg))
                .switchIfEmpty(LinksExceptionTcp.errors("客户端id为空"))
                .filter(c -> this.isAuthSuccess(c, msg))
                .switchIfEmpty(LinksExceptionTcp.errors("账号密码认证错误"));
    }

    @Override
    public boolean isDecoderSuccess(Channel channel, MqttConnectMessage msg) {
        if (msg.decoderResult().isFailure()) {
            Throwable cause = msg.decoderResult().cause();
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                // 不支持的协议版本
                MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false), null);
                channel.writeAndFlush(connAckMessage);
                channel.close();
            } else if (cause instanceof MqttIdentifierRejectedException) {
                // 不合格的clientId
                MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false), null);
                channel.writeAndFlush(connAckMessage);
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean clientIdIsNotNull(Channel channel, MqttConnectMessage msg) {
        if (StrUtil.isBlank(msg.payload().clientIdentifier())) {
            MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false), null);
            channel.writeAndFlush(connAckMessage);
            return false;
        }
        return true;
    }

    @Override
    public boolean isAuthSuccess(Channel channel, MqttConnectMessage msg) {
        if (brokerProperties.getMqttPasswordMust()) {
            String username = msg.payload().userName();
            String password = msg.payload().passwordInBytes() == null ? null : new String(msg.payload().passwordInBytes(), CharsetUtil.UTF_8);
            if (true) {
                MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false), null);
                channel.writeAndFlush(connAckMessage);
                channel.close();
                return false;
            }
        }
        return true;
    }

}
