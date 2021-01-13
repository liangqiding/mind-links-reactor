package com.mind.links.netty.mqtt.mqttFilter;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnectMessage;

/**
 * @author qiding
 */
public interface IMqttFilter {

    /**
     * 过滤错误链接，非法链接，无认证连接 等等
     *
     * @param channel 管道
     * @param msg     消息
     */
    void connectFilter(Channel channel, MqttConnectMessage msg);

    /**
     * 解码器是否故障
     *
     * @param channel 管道
     * @param msg     消息
     * @return true or false
     */
    boolean isDecoderFailure(Channel channel, MqttConnectMessage msg);

    /**
     * clientId为空或null的情况, 这里要求客户端必须提供clientId, 不管cleanSession是否为1, 此处没有参考标准协议实现
     *
     * @param channel 管道
     * @param msg     消息
     * @return true or false
     */
    boolean clientIdIsNull(Channel channel, MqttConnectMessage msg);

    /**
     * 用户名和密码验证, 这里要求客户端连接时必须提供用户名和密码
     *
     * @param channel 管道
     * @param msg     消息
     * @return true or false
     */
    boolean isAuthFailure(Channel channel, MqttConnectMessage msg);
}
