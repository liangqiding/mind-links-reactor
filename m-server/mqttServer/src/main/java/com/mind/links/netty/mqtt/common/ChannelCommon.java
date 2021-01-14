package com.mind.links.netty.mqtt.common;

import com.mind.links.netty.mqtt.config.BrokerProperties;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * date: 2021-01-13 16:23
 * description
 *
 * @author qiDing
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ChannelCommon {

    private final BrokerProperties brokerProperties;

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static Map<String, ChannelId> channelIdMap = new ConcurrentHashMap<>(2 << 4);

    public void saveChannel(ChannelHandlerContext ctx) {
        log.debug("保存channel" + ctx.channel());
        ChannelCommon.channelGroup.add(ctx.channel());
        ChannelCommon.channelIdMap.put(brokerProperties.getId() + "_" + ctx.channel().id().asLongText(), ctx.channel().id());
    }

    public void removeChannel(ChannelHandlerContext ctx) {
        log.debug("移除channel" + ctx.channel());
        ChannelCommon.channelGroup.remove(ctx.channel());
        ChannelCommon.channelIdMap.remove(brokerProperties.getId() + "_" + ctx.channel().id().asLongText());
    }


    public static void channelClose(Channel channel) {
        log.debug("channelClose" + channel);
        channel.close();
    }

    public static Object getClientId(Channel channel) {
        return channel.attr(AttributeKey.valueOf("clientId")).get();
    }

}
