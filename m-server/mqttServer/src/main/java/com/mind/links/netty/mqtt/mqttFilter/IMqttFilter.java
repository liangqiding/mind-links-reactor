package com.mind.links.netty.mqtt.mqttFilter;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import reactor.core.publisher.Mono;

/**
 * @author qiding
 */
public interface IMqttFilter {

    /**
     * 拦截器入口
     *
     * @param channel 管道
     * @param msg     消息
     * @return Mono<Channel>
     */
    Mono<Channel> filter(final Channel channel, final MqttConnectMessage msg);

    /**
     * 解码器是否故障
     *
     * @param channel 管道
     * @param msg     消息
     * @return true or false
     */
    boolean isDecoderSuccess(Channel channel, MqttConnectMessage msg);

    /**
     * clientId为空或null的情况, 这里要求客户端必须提供clientId, 不管cleanSession是否为1, 此处没有参考标准协议实现
     *
     * @param channel 管道
     * @param msg     消息
     * @return true or false
     */
    boolean clientIdIsNotNull(Channel channel, MqttConnectMessage msg);

    /**
     * 用户名和密码验证, 这里要求客户端连接时必须提供用户名和密码
     *
     * @param channel 管道
     * @param msg     消息
     * @return true or false
     */
    boolean isAuthSuccess(Channel channel, MqttConnectMessage msg);
}
