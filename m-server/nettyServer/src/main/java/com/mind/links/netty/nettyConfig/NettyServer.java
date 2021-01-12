package com.mind.links.netty.nettyConfig;

import com.mind.links.netty.nettyInitializer.NettyServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Netty配置类
 *
 * @author qiding
 */
@Component
@Slf4j
@ApiModel("Netty配置类")
public class NettyServer {

    @ApiModelProperty("react模式 主线程池")
    private final EventLoopGroup BOSS = new NioEventLoopGroup();

    @ApiModelProperty("IO 操作线程池")
    private final EventLoopGroup WORKER = new NioEventLoopGroup();

    @ApiModelProperty("通讯模式")
    private final Class<? extends ServerChannel> COMMUNICATION_MODE = NioServerSocketChannel.class;

    @ApiModelProperty("编码解码器")
    private final NettyServerChannelInitializer CHANNEL_INITIALIZER = new NettyServerChannelInitializer();

    @ApiModelProperty("临时存放已完成三次握手的请求的队列的最大长度")
    private final Integer BACKLOG = 128;

    @Value("${netty.port}")
    @ApiModelProperty("监听端口")
    private Integer port;

    public void start() throws Exception {
        String address = InetAddress.getLocalHost().getHostAddress();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(BOSS, WORKER)
                    .channel(COMMUNICATION_MODE)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(CHANNEL_INITIALIZER)
                    .option(ChannelOption.SO_BACKLOG, BACKLOG)
                    // 保持长连接，2小时无数据激活心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 绑定端口，开始接收进来的连接
            ChannelFuture future = bootstrap.bind().sync();
            log.info("===netty服务器开始监听端口：" + address + ":" + port+",临时存放已完成三次握手的请求的队列的最大长度"+BACKLOG);
            // 关闭channel和块，直到它被关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            BOSS.shutdownGracefully();
            WORKER.shutdownGracefully();
        }
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        BOSS.shutdownGracefully().syncUninterruptibly();
        WORKER.shutdownGracefully().syncUninterruptibly();
        log.info("Close cim server success!!!");
    }
}

