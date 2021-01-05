package com.mind.links.netty.nettyHandler;

import com.alibaba.fastjson.JSON;
import com.mind.links.netty.common.CtxHandler;
import com.mind.links.netty.messageHandler.MessageHandlerImpl;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


/**
 * io业务处理
 *
 * @author qiding
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class NettyServerHandler extends SimpleChannelInboundHandler<Object> {

    private static MessageHandlerImpl messageHandler;

    @Autowired
    public void setMessageHandler(MessageHandlerImpl mh) {
        messageHandler = mh;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Mono.just(ctx)
                .flatMap(CtxHandler::getMsgMap)
                .flatMap(msgMap -> messageHandler.webSocketHandler(msgMap, ctx, msg))
                .flatMap(msgMap -> messageHandler.socketHandler(msgMap, ctx, msg))
                .subscribe(msgMap -> log.info(JSON.toJSONString(msgMap)));
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Mono.just(evt)
                .filter(evt0 -> evt0 instanceof IdleStateEvent)
                .flatMap(b -> CtxHandler.getMsgMap(ctx))
                .subscribe(msgMap -> {
                    final IdleStateEvent event = (IdleStateEvent) evt;
                    if (event.state() == IdleState.READER_IDLE) {
                        msgMap.put("event", "READER_IDLE");
                        msgMap.put("error", "读超时");
                        ctx.disconnect();
                    } else if (event.state() == IdleState.WRITER_IDLE) {
                        msgMap.put("event", "WRITER_IDLE");
                        msgMap.put("error", "写超时");
                        ctx.disconnect();
                    } else if (event.state() == IdleState.ALL_IDLE) {
                        msgMap.put("event", "ALL_IDLE");
                        msgMap.put("error", "总超时");
                        ctx.disconnect();
                    }
                    log.error(JSON.toJSONString(msgMap));
                });
    }
}

