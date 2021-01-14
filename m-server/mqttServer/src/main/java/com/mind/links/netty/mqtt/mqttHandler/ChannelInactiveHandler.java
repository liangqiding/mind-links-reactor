package com.mind.links.netty.mqtt.mqttHandler;

import com.mind.links.netty.mqtt.common.ChannelCommon;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final ChannelCommon channelCommon;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelInactive:" + ctx.name());
        super.channelInactive(ctx);
        channelCommon.removeChannel(ctx);
    }
}
