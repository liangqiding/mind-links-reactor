package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import org.apache.ibatis.annotations.Param;
import reactor.core.publisher.Mono;


/**
 * 消息处理
 *
 * @author qiding
 */
public interface IMqttMessageHandler {

    /**
     * 连接报文处理
     *
     * @param ctx 通道处理程序上下文
     * @param msg     消息
     * @return Void
     */
    default Mono<Void> connect(final ChannelHandlerContext ctx,final MqttMessage msg) {
        return Mono.empty();
    }

    /**
     * ping请求
     *
     * @param ctx 通道处理程序上下文
     * @return Mono<Void>
     */
    default Mono<Void> pingReq(final ChannelHandlerContext ctx) {
        return Mono.empty();
    }

    /**
     * 发布消息处理
     *
     * @param ctx 通道处理程序上下文
     * @param msg     消息
     * @return Mono<Void>
     */
    default Mono<Void> publish(final ChannelHandlerContext ctx,final MqttMessage msg) {
        return Mono.empty();
    }
}
