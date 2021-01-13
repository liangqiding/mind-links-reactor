package com.mind.links.netty.nettyHandler;

import com.alibaba.fastjson.JSON;
import com.mind.links.common.enums.LinksExceptionEnum;
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
                        msgMap.put("event", "重连");
                        log.info(JSON.toJSONString(msgMap));
                        return;
                    }
                    cause.printStackTrace();
                    ctx.close();
                    msgMap.put("event", "全局异常");
                    msgMap.put("error", cause);
                    log.error(JSON.toJSONString(msgMap), cause);
                });
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                Mono.just(future)
                        .filter(channelFuture -> !channelFuture.isSuccess())
                        .flatMap(f -> CtxHandler.getMsgMap(ctx))
                        .subscribe(msgMap -> {
                            System.out.println();
                            msgMap.put("event", "连接异常");
                            msgMap.put("error", future.cause());
                            log.info(JSON.toJSONString(msgMap));
                        });
            }
        }));
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ctx.write(msg, promise.addListener((ChannelFutureListener) future -> {
            Mono.just(future)
                    .filter(channelFuture -> !channelFuture.isSuccess())
                    .flatMap(f -> CtxHandler.getMsgMap(ctx))
                    .subscribe(msgMap -> {
                        System.out.println();
                        msgMap.put("event", "发送异常");
                        msgMap.put("error", future.cause());
                        log.info(JSON.toJSONString(msgMap));
                    });
        }));
    }
}
