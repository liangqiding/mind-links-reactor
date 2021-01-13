package com.mind.links.netty.mqtt.mqttHandler;

import com.mind.links.netty.mqtt.mqttHandler.protocolHandler.ConnectHandler;
import com.mind.links.netty.mqtt.mqttHandler.protocolHandler.PingRegHandler;
import io.netty.channel.*;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * MQTT消息处理
 *
 * @author qiding
 */
@ChannelHandler.Sharable
@Slf4j
@Component
@RequiredArgsConstructor
public class MqttBrokerHandler extends SimpleChannelInboundHandler<MqttMessage> {

    private final ConnectHandler connectHandler;

    private final PingRegHandler pingRegHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
        log.debug("channelInactive:" + ctx.name());
        if (msg.decoderResult().isFailure()) {
            Throwable cause = msg.decoderResult().cause();
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                ctx.writeAndFlush(MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false),
                        null));
            } else if (cause instanceof MqttIdentifierRejectedException) {
                ctx.writeAndFlush(MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false),
                        null));
            }
            ctx.close();
        }

        switch (msg.fixedHeader().messageType()) {
            case CONNECT:
                log.info("CONNECT");
                connectHandler.connect(ctx.channel(), (MqttConnectMessage) msg);
                break;
            case CONNACK:
                break;
            case PUBLISH:
                log.info("PUBLISH");
                this.debug((MqttPublishMessage) msg);
                break;
            case PUBACK:
                log.info("PUBACK");
                break;
            case PUBREC:
                log.info("PUBREC");
                break;
            case PUBREL:
                log.info("PUBREL");
                break;
            case PUBCOMP:
                log.info("PUBCOMP");
                break;
            case SUBSCRIBE:
                log.info("SUBSCRIBE");
                break;
            case SUBACK:
                break;
            case UNSUBSCRIBE:
                log.info("UNSUBSCRIBE");
                break;
            case UNSUBACK:
                log.info("UNSUBACK");
                break;
            case PINGREQ:
                log.info("PING_REQ");
                pingRegHandler.processPingReq(ctx.channel());
                break;
            case PINGRESP:
                break;
            case DISCONNECT:
                log.info("DISCONNECT");
                break;
            default:
                break;
        }
    }

    public void debug(MqttPublishMessage mqttPublishMessage) {
        String name = mqttPublishMessage.variableHeader().topicName();
        byte[] messageBytes = new byte[mqttPublishMessage.payload().readableBytes()];
        mqttPublishMessage.payload().getBytes(mqttPublishMessage.payload().readerIndex(), messageBytes);
        String msg = new String(messageBytes);
        log.info("topic:" + name + ",msg:" + msg);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("用户超时事件触发" + ctx.channel().id());
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                Channel channel = ctx.channel();
                String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
                // 发送遗嘱消息
                log.info("发送遗嘱消息" + clientId);
                ctx.close();
            }
        }
    }
}
