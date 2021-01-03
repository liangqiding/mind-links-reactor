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
 * description : TODO  连接校验认证
 *
 * @author : qiDing
 * date: 2021-01-02 10:57
 * @version v1.0.0
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public class ChannelActiveHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Mono.just(ctx).flatMap(CtxHandler::getMsgMap)
                .subscribe(msgMap -> {
                    System.out.println();
                    if (ConcurrentContext.CHANNEL_MAP.containsKey(ctx.channel().id())) {
                        log.info("===是已连接的:{}", JSON.toJSONString(msgMap));
                    } else {
                        ConcurrentContext.CHANNEL_MAP.put(ctx.channel().id(), ctx);
                        log.info("===:{}", JSON.toJSONString(msgMap));
                    }
                    log.info("===连接通道数量: {}" , ConcurrentContext.CHANNEL_MAP.size());
                });
    }
}
