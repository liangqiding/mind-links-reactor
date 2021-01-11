
package com.mind.links.netty.nettyMqtt;

import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import java.io.IOException;
import java.util.Map;

/**
 * MQTT消息处理
 */
@IocBean
@ChannelHandler.Sharable
@Slf4j
public class BrokerHandler extends SimpleChannelInboundHandler<MqttMessage> {

    @Inject
    private BrokerProperties brokerProperties;
    @Inject
    private ChannelGroup channelGroup;
    @Inject
    private Map<String, ChannelId> channelIdMap;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channelGroup.add(ctx.channel());
        this.channelIdMap.put(brokerProperties.getId() + "_" + ctx.channel().id().asLongText(), ctx.channel().id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        this.channelGroup.remove(ctx.channel());
        this.channelIdMap.remove(brokerProperties.getId() + "_" + ctx.channel().id().asLongText());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
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
            return;
        }

        switch (msg.fixedHeader().messageType()) {
            case CONNECT:
                log.info("CONNECT");
                break;
            case CONNACK:
                break;
            case PUBLISH:
                log.info("PUBLISH");
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
