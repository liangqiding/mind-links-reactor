package com.mind.links.netty.nettyHandler;

import com.alibaba.fastjson.JSON;
import com.mind.links.netty.common.ConcurrentContext;
import com.mind.links.netty.common.CtxHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


/**
 * description: 连接中断管理
 * @author qiDing
 * date: 2021-01-02 15:27
 * @version v1.0.0
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class ChannelInactiveHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Mono.just(ctx).flatMap(CtxHandler::getMsgMap)
                .subscribe(msgMap -> {
                    System.out.println();
                    if (ConcurrentContext.CHANNEL_MAP.containsKey(ctx.channel().id())) {
                        ConcurrentContext.CHANNEL_MAP.remove(ctx.channel().id());
                        msgMap.put("event","连接断开");
                        log.info(JSON.toJSONString(msgMap));
                    }
                });
    }
}
