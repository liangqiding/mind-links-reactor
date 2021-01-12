
package com.mind.links.netty.nettyMqtt.handler;

import com.mind.links.netty.nettyMqtt.config.BrokerProperties;
import com.mind.links.netty.nettyMqtt.BrokerServer;
import com.mind.links.netty.nettyMqtt.config.ProtocolProcess;
import io.netty.channel.*;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * MQTT消息处理
 */
@ChannelHandler.Sharable
@Slf4j
@Component
public class BrokerHandler extends SimpleChannelInboundHandler<MqttMessage> {

    private static BrokerProperties brokerProperties;
    private static ProtocolProcess protocolProcess;


    @Autowired
    public void setBrokerProperties(BrokerProperties b) {
        brokerProperties = b;
    }

    @Autowired
    public void setProtocolProcess(ProtocolProcess p) {
        protocolProcess = p;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.debug("channelActive:" + ctx.name());
        BrokerServer.channelGroup.add(ctx.channel());
        BrokerServer.channelIdMap.put(brokerProperties.getId() + "_" + ctx.channel().id().asLongText(), ctx.channel().id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelInactive:" + ctx.name());
        super.channelInactive(ctx);
        BrokerServer.channelGroup.remove(ctx.channel());
        BrokerServer.channelIdMap.remove(brokerProperties.getId() + "_" + ctx.channel().id().asLongText());
    }

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
                protocolProcess.connect().processConnect(ctx.channel(), (MqttConnectMessage) msg);
                break;
            case CONNACK:
                break;
            case PUBLISH:
                log.info("PUBLISH");
                this.debug((MqttPublishMessage)msg);
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
                log.info("PINGREQ");
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

        log.info("topic:"+name+",msg:"+msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            // 远程主机强迫关闭了一个现有的连接的异常
            ctx.close();
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
//            if (idleStateEvent.state() == IdleState.ALL_IDLE) {
//                Channel channel = ctx.channel();
//                String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
//                // 发送遗嘱消息
//                if (this.protocolProcess.getSessionStoreService().containsKey(clientId)) {
//                    SessionStore sessionStore = this.protocolProcess.getSessionStoreService().get(clientId);
//                    if (sessionStore.getWillMessage() != null) {
//                        this.protocolProcess.publish().processPublish(ctx.channel(), sessionStore.getWillMessage());
//                    }
//                }
            ctx.close();
//            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
