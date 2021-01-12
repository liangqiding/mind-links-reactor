package com.mind.links.netty.mqtt.mqttHandler;


import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
        cause.printStackTrace();
        log.error("exceptionCaught:",cause);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if(!future.isSuccess()){
                    future.cause().printStackTrace();
                    log.error("connect exceptionCaught",future.cause());
                }
            }
        }));
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ctx.write(msg, promise.addListener((ChannelFutureListener) future -> {
            if(!future.isSuccess()){
                future.cause().printStackTrace();
                log.error("write exceptionCaught{}",future);
            }
        }));
    }
}
