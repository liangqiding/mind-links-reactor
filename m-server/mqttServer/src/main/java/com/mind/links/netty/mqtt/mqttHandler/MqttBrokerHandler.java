package com.mind.links.netty.mqtt.mqttHandler;

import com.mind.links.netty.mqtt.common.MqttMsgTypeEnum;
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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;


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

    private final Scheduler myScheduler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
        log.debug("channelInactive:" + ctx.name());
        Mono.just(msg)
                .filter(mqttMessage -> this.decoderResultIsSuccess(ctx, msg))
                .switchIfEmpty(error(ctx))
                .flatMap(mqttMessage -> MqttMsgTypeEnum.msgHandler(mqttMessage.fixedHeader().messageType().value(),ctx,mqttMessage))
                .subscribe();
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

    private boolean decoderResultIsSuccess(ChannelHandlerContext ctx, MqttMessage msg) {
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
            return false;
        }
        return true;
    }

    private  <T> Mono<T> error(ChannelHandlerContext ctx) {
        return ExceptionHandler.errors("channelRead0-解码器异常");
    }
}
