package com.mind.links.netty.mqtt.mqttHandler.protocolHandler;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import reactor.core.publisher.Mono;


/**
 * 消息处理
 *
 * @author qiding
 */
public interface IMqttMessageHandler {

    /**
     * 1 连接报文处理
     *
     * @param channel 上下文
     * @param msg 消息
     * @return Void
     */
    default Mono<Channel> connect(Channel channel, final MqttMessage msg) {
        return Mono.empty();
    }

    /**
     * 2 确认连接请求
     *
     * @param channel 上下文
     * @param msg 消息
     * @return Void
     */
    default Mono<Channel>  connAck( Channel channel, final MqttMessage msg) {
        return Mono.empty();
    }

    /**
     * 3 发布消息处理
     *
     * @param channel 上下文
     * @param msg 消息
     * @return Mono<Channel> 
     */
    default Mono<Channel>  publish( Channel channel, final MqttMessage msg) {
        return Mono.empty();
    }

    /**
     * 4 发布确认
     *
     * @param channel 上下文
     * @param msg 消息
     * @return Mono<Channel> 
     */
    default Mono<Channel>  pubAck( Channel channel, final MqttMessage msg) {
        return Mono.empty();
    }

    /**
     * 5 发布已接受（qos2 第一步）
     *
     * @param channel 上下文
     * @param msg 消息
     * @return Mono<Channel> 
     */
    default Mono<Channel>  pubRec( Channel channel, final MqttMessage msg) {
        return Mono.empty();
    }

    /**
     * 6 发布已释放（qos2 第二步）
     *
     * @param channel 上下文
     * @param msg 消息
     * @return Mono<Channel> 
     */
    default Mono<Channel>  pubRel( Channel channel, final MqttMessage msg) {
        return Mono.empty();
    }


    /**
     * 7 发布完成（qos2 第三步)
     *
     * @param channel 上下文
     * @param msg 消息
     * @return Mono<Channel> 
     */
    default Mono<Channel>  pubComp( Channel channel, final MqttMessage msg) {
        return Mono.empty();
    }

    /**
     * 8 订阅请求
     *
     * @param channel 上下文
     * @param msg 消息
     * @return Mono<Channel> 
     */
    default Mono<Channel>  subscribe( Channel channel, final MqttMessage msg) {
        return Mono.empty();
    }

    /**
     * 9 订阅确认
     *
     * @param channel 上下文
     * @param msg 消息
     * @return Mono<Channel> 
     */
    default Mono<Channel>  subAck( Channel channel, final MqttMessage msg) {
        return Mono.empty();
    }

    /**
     * 10 取消订阅
     *
     * @param channel 上下文
     * @param msg 消息
     * @return Mono<Channel> 
     */
    default Mono<Channel>  unsubscribe( Channel channel, final MqttMessage msg) {
        return Mono.empty();
    }

    /**
     * 11 取消订阅确认
     *
     * @param channel 上下文
     * @param msg 消息
     * @return Mono<Channel> 
     */
    default Mono<Channel>  unSubAck( Channel channel, final MqttMessage msg) {
        return Mono.empty();
    }

    /**
     * 12 ping请求
     *
     * @param channel 上下文
     * @return Mono<Channel> 
     */
    default Mono<Channel>  pingReq( Channel channel) {
        return Mono.empty();
    }

    /**
     * 13 ping响应
     *
     * @param channel 上下文
     * @return Mono<Channel> 
     */
    default Mono<Channel>  pingResp( Channel channel) {
        return Mono.empty();
    }

    /**
     * 14 端开链接
     *
     * @param channel 上下文
     * @return Mono<Channel> 
     */
    default Mono<Channel>  disconnect( Channel channel) {
        return Mono.empty();
    }
}
