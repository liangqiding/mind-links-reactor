package com.mind.links.netty.nettyHandler;

import com.alibaba.fastjson.JSON;
import com.mind.links.common.enums.LinksExceptionEnum;
import com.mind.links.netty.common.ConcurrentContext;
import com.mind.links.netty.common.CtxHandler;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.net.SocketAddress;


/**
 * description : TODO 异常处理
 *
 * @author : qiDing
 * date: 2021-01-03 08:45
 * @version v1.0.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class ExceptionHandler extends ChannelDuplexHandler {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Mono.just(ctx)
                .flatMap(CtxHandler::getMsgMap)
                .subscribe(msgMap -> {
                    System.out.println();
                    if (LinksExceptionEnum.RECONNECTION.getMsg().equals(cause.getMessage())) {
                        log.info("===触发重连===");
                        return;
                    }
                    cause.printStackTrace();
                    ctx.close();
                    log.error("***连接异常,已断开连接:{}", JSON.toJSONString(msgMap));
                    log.info("===连接通道数量: {}", ConcurrentContext.CHANNEL_MAP.size());
                });
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception  {
        ctx.connect(remoteAddress, localAddress, promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (!future.isSuccess()) {
                    log.error("***连接异常: ", future.cause());
                }
            }
        }));
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ctx.write(msg, promise.addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                log.error("***消息发送异常: ", future.cause());
            }
        }));
    }
}
