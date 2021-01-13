package com.mind.links.netty.mqtt.mqttInitializer;

import com.mind.links.netty.mqtt.mqttHandler.ChannelActiveHandler;
import com.mind.links.netty.mqtt.mqttHandler.ChannelInactiveHandler;
import com.mind.links.netty.mqtt.mqttHandler.ExceptionHandler;
import com.mind.links.netty.mqtt.mqttHandler.MqttBrokerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Netty编码解码器
 *
 * @author qiding
 */
@ApiModel("编码解码器")
@Component
@RequiredArgsConstructor
public class MqttServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @ApiModelProperty("通道主动处理程序(单例)")
    private final ChannelActiveHandler channelActiveHandler;

    @ApiModelProperty("频道无效的处理程序(单例)")
    private final ChannelInactiveHandler channelInactiveHandler;

    @ApiModelProperty("IO处理程序(单例)")
    private final MqttBrokerHandler mqttBrokerHandler;

    @ApiModelProperty("异常处理程序(单例)")
    private final ExceptionHandler exceptionHandler;

    @ApiModelProperty("MQTT编码器(单例)")
    private static final MqttEncoder MQTT_ENCODER = MqttEncoder.INSTANCE;


    @Override
    protected void initChannel(SocketChannel channel) {

        final NettyInitializerProperties nip = new NettyInitializerProperties();

        channel.pipeline()
                .addLast(channelActiveHandler.getClass().getSimpleName(), channelActiveHandler)
                .addLast(channelInactiveHandler.getClass().getSimpleName(), channelInactiveHandler)
                .addLast(nip.idleStateHandler.getClass().getSimpleName(), nip.idleStateHandler)
                .addLast(nip.mqttDecoder.getClass().getSimpleName(), nip.mqttDecoder)
                .addLast(MQTT_ENCODER.getClass().getSimpleName(), MQTT_ENCODER)
                .addLast(mqttBrokerHandler.getClass().getSimpleName(), mqttBrokerHandler)
                .addLast(exceptionHandler.getClass().getSimpleName(), exceptionHandler);
    }


    @ApiModel("通用编码解码器配置（多例）")
    public static class NettyInitializerProperties {

        @ApiModelProperty("心跳机制(多例)")
        volatile IdleStateHandler idleStateHandler = new IdleStateHandler(10, 0, 0);

        @ApiModelProperty("MQTT解码器(多例)")
        volatile MqttDecoder mqttDecoder = new MqttDecoder();

    }
}


