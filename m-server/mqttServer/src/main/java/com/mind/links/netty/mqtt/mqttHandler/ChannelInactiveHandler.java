package com.mind.links.netty.mqtt.mqttHandler;

import com.mind.links.netty.mqtt.mqttServer.MqttBrokerServer;
import com.mind.links.netty.mqtt.config.BrokerProperties;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * description: 连接中断管理
 * @author qiDing
 * date: 2021-01-02 15:27
 * @version v1.0.0
 */
@Component
@ChannelHandler.Sharable
@Slf4j
@RequiredArgsConstructor
public class ChannelInactiveHandler extends ChannelInboundHandlerAdapter {

    private final BrokerProperties brokerProperties;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("channelInactive:" + ctx.name());
        MqttBrokerServer.channelGroup.remove(ctx.channel());
        MqttBrokerServer.channelIdMap.remove(brokerProperties.getId() + "_" + ctx.channel().id().asLongText());
    }
}
