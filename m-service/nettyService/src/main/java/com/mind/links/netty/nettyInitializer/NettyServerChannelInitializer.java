package com.mind.links.netty.nettyInitializer;

import com.mind.links.netty.nettyHandler.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;


/**
 * Netty编码解码器
 *
 * @author qiding
 */
@ApiModel("编码解码器")
@Component
public class NettyServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) {
        NettyInitializerProperties nip = new NettyInitializerProperties();
        channel.pipeline()
                .addLast(NettyInitializerProperties.CHANNEL_ACTIVE_HANDLER.getClass().getSimpleName(), NettyInitializerProperties.CHANNEL_ACTIVE_HANDLER)
                .addLast(NettyInitializerProperties.CHANNEL_INACTIVE_HANDLER.getClass().getSimpleName(), NettyInitializerProperties.CHANNEL_INACTIVE_HANDLER)
                .addLast(nip.socketChooseHandler.getClass().getSimpleName(), nip.socketChooseHandler)
                .addLast(nip.idleStateHandler.getClass().getSimpleName(), nip.idleStateHandler)
                .addLast(NettyInitializerProperties.NETTY_SERVER_HANDLER.getClass().getSimpleName(), NettyInitializerProperties.NETTY_SERVER_HANDLER)
                .addLast(NettyInitializerProperties.EXCEPTION_HANDLER.getClass().getSimpleName(), NettyInitializerProperties.EXCEPTION_HANDLER);
    }

    /**
     * 为了最大程度地减少对象的创建
     */
    @ApiModel("通用编码解码器配置")
    public static class NettyInitializerProperties {

        @ApiModelProperty("心跳机制(多例)")
        volatile IdleStateHandler idleStateHandler = new IdleStateHandler(60, 0, 0);

        @ApiModelProperty("协议选择处理程序(多例)")
        volatile SocketChooseHandler socketChooseHandler = new SocketChooseHandler();

        @ApiModelProperty("通道主动处理程序(单例)")
        private final static ChannelActiveHandler CHANNEL_ACTIVE_HANDLER = new ChannelActiveHandler();

        @ApiModelProperty("频道无效的处理程序(单例)")
        private final static ChannelInactiveHandler CHANNEL_INACTIVE_HANDLER = new ChannelInactiveHandler();

        @ApiModelProperty("IO处理程序(单例)")
        private final static NettyServerHandler NETTY_SERVER_HANDLER = new NettyServerHandler();

        @ApiModelProperty("异常处理程序(单例)")
        private final static ExceptionHandler EXCEPTION_HANDLER = new ExceptionHandler();

    }
}


