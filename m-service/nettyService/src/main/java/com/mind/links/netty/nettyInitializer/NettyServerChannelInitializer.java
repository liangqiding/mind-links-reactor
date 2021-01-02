package com.mind.links.netty.nettyInitializer;

import com.mind.links.netty.nettyHandler.ChannelActiveHandler;
import com.mind.links.netty.nettyHandler.ChannelInactiveHandler;
import com.mind.links.netty.nettyHandler.NettyServerHandler;
import com.mind.links.netty.nettyHandler.SocketChooseHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;


/**
 * Netty获取、发送数据及处理
 *
 * @author qiding
 */
@ApiModel("编码解码器")
@Component

public class NettyServerChannelInitializer extends ChannelInitializer<SocketChannel> {


    @ApiModelProperty("心跳机制")
    volatile IdleStateHandler idleStateHandler= new IdleStateHandler(60, 0, 0);

    @ApiModelProperty("通道主动处理程序")
    volatile ChannelActiveHandler channelActiveHandler= new ChannelActiveHandler();

    @ApiModelProperty("频道无效的处理程序")
    volatile ChannelInactiveHandler channelInactiveHandler= new ChannelInactiveHandler();

    @ApiModelProperty("协议选择处理程序")
    volatile SocketChooseHandler socketChooseHandler= new SocketChooseHandler();

    @ApiModelProperty("IO处理程序")
    volatile NettyServerHandler nettyServerHandler= new NettyServerHandler();


    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        this.init();

        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(idleStateHandler.getClass().getSimpleName(), idleStateHandler);

        pipeline.addLast(channelActiveHandler.getClass().getSimpleName(), channelActiveHandler);

        pipeline.addLast(channelInactiveHandler.getClass().getSimpleName(), channelInactiveHandler);

        pipeline.addLast(socketChooseHandler.getClass().getSimpleName(), socketChooseHandler);

        channel.pipeline().addLast(nettyServerHandler.getClass().getSimpleName(), nettyServerHandler);
    }

    private  void init() {
        this.idleStateHandler = new IdleStateHandler(60, 0, 0);
        this.channelActiveHandler = new ChannelActiveHandler();
        this.channelInactiveHandler = new ChannelInactiveHandler();
        this.socketChooseHandler = new SocketChooseHandler();
        this.nettyServerHandler = new NettyServerHandler();
    }
}


