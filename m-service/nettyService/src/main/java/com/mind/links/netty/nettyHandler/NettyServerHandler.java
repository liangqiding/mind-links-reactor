package com.mind.links.netty.nettyHandler;

import com.mind.links.common.utils.LinksDateUtils;
import com.mind.links.netty.common.ConcurrentContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * io业务处理
 *
 * @author qiding
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class NettyServerHandler extends SimpleChannelInboundHandler<Object> {

    @ApiModelProperty("服务器地址")
    private static String address;

    @ApiModelProperty("服务器端口")
    private static String port;

    @Value("${netty.port}")
    private void setPort(String p) {
        port = p;
    }

    static {
        try {
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    int i = 0;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    /**
     * @param ctx ·
     *            DESCRIPTION: 有客户端发消息会触发此函数
     * @return: void
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();
        // WebSocket消息处理
        System.out.println(i++);
        if (msg instanceof TextWebSocketFrame) {
            //第一次连接成功后，给客户端发送消息
            TextWebSocketFrame msgText = (TextWebSocketFrame) msg;
            Channel channel = ctx.channel();
            channel.writeAndFlush(new TextWebSocketFrame(LinksDateUtils.dateFormat() + " 服务器[" + address + ":" + port + "]----收到消息：" + msgText.text()));
            //获取当前channel绑定的IP地址
            InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
            String address = ipSocket.getAddress().getHostAddress();
            log.info("address为:" + address + ",msg-------------->>>>>>>>>>>>" + msgText.text());
            //将IP和channel的关系保存
        }
        // Socket消息处理
        else {
            log.info("Socket消息处理=================================");
            ByteBuf buff = (ByteBuf) msg;
            String socketInfo = buff.toString(CharsetUtil.UTF_8).trim();
            ;
            log.info("收到socket消息：" + socketInfo);
        }
        //文本消息
//        if (msg instanceof TextWebSocketFrame) {
//            //第一次连接成功后，给客户端发送消息
//            TextWebSocketFrame msgText = (TextWebSocketFrame) msg;
//            //获取当前channel绑定的IP地址
//            InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
//            String address = ipSocket.getAddress().getHostAddress();
//            log.info("address为:" + address + ",msg-------------->>>>>>>>>>>>" + msgText.text());
//            ctx.writeAndFlush(new TextWebSocketFrame(LinksDateUtils.dateFormat() + " 服务器[" + address + ":" + port + "]----收到消息：" + msgText.text())).addListeners((ChannelFutureListener) future -> {
//                if (!future.isSuccess()) {
//                    log.error("IO error,close Channel");
//                    future.channel().close();
//                }
//            });
//        }
//        //二进制消息
//        if (msg instanceof BinaryWebSocketFrame) {
//            log.info("收到二进制消息：" + ((BinaryWebSocketFrame) msg).content().readableBytes()+"\n");
//            BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(Unpooled.buffer().writeBytes("hello".getBytes()));
//            //给客户端发送的消息
//            ctx.writeAndFlush(binaryWebSocketFrame).addListeners((ChannelFutureListener) future -> {
//                if (!future.isSuccess()) {
//                    log.error("IO error,close Channel");
//                    future.channel().close();
//                }
//            });
//        }

        //ping消息
        if (msg instanceof PongWebSocketFrame) {
            log.info("客户端ping成功");
        }
        //关闭消息
        if (msg instanceof CloseWebSocketFrame) {
            log.info("客户端关闭，通道关闭");
            ctx.channel().close();
        }
//        log.info("客户端收到消息: {}", msg.toString());
    }

    /**
     * @param msg       需要发送的消息内容
     * @param channelId 连接通道唯一id
     *                  DESCRIPTION: 服务端给客户端发送消息
     * @return: void
     */
    public void channelWrite(ChannelId channelId, Object msg) throws Exception {
        ChannelHandlerContext ctx = ConcurrentContext.CHANNEL_MAP.get(channelId);
        if (ctx == null) {
            log.info("通道【" + channelId + "】不存在");
            return;
        }
        //将客户端的信息直接返回写入ctx
        ctx.write(msg);
        //刷新缓存区
        ctx.flush();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String socketString = ctx.channel().remoteAddress().toString();
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.info("Client: " + socketString + " READER_IDLE 读超时");
                ctx.disconnect();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                log.info("Client: " + socketString + " WRITER_IDLE 写超时");
                ctx.disconnect();
            } else if (event.state() == IdleState.ALL_IDLE) {
                log.info("Client: " + socketString + " ALL_IDLE 总超时");
                ctx.disconnect();
            }
        }
    }

    /**
     * 连接错误时调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info(ctx.channel().id() + " 发生了错误," + "此时连通数量: " + ConcurrentContext.CHANNEL_MAP.size());
        if ("重连".equals(cause.getMessage())) {
            log.info("触发重连");
            return;
        }
        cause.printStackTrace();
        ctx.close();
    }


}

