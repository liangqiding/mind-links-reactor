package com.mind.links.netty.mqtt.mqttHandler;

import com.mind.links.netty.mqtt.common.ChannelCommon;
import com.mind.links.netty.mqtt.config.BrokerProperties;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * description : 连接校验认证
 *
 * @author : qiDing
 * date: 2021-01-02 10:57
 */
@Component
@Slf4j
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class ChannelActiveHandler extends ChannelInboundHandlerAdapter {

    private final ChannelCommon channelCommon;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelActive:" + ctx.name());
        super.channelActive(ctx);
        channelCommon.saveChannel(ctx);
    }

}
