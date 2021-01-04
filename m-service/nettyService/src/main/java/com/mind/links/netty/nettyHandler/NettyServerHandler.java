package com.mind.links.netty.nettyHandler;

import com.alibaba.fastjson.JSON;
import com.mind.links.common.response.ResponseResult;
import com.mind.links.netty.common.ConcurrentContext;
import com.mind.links.netty.common.CtxHandler;
import com.mind.links.netty.kafka.KafkaProducers;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;


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

    @ApiModelProperty("socket TOPIC")
    private static final String SOCKET_TOPIC = "socket";

    @ApiModelProperty("webSocket TOPIC")
    private static final String WEB_SOCKET_TOPIC = "webSocket";

    @Value("${netty.port}")
    private void setPort(String p) {
        port = p;
    }

    private static KafkaProducers kafkaProducer;

    @Autowired
    public void setKafkaProducer(KafkaProducers k) {
        kafkaProducer = k;
    }

    static {
        try {
            // 获取本地ip+端口,便于日记中bug定位
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        Mono<HashMap<String, Object>> publish = Mono.just(ctx).flatMap(CtxHandler::getMsgMap).publish(map -> map);

        publish.subscribe(m -> {
            if (msg instanceof TextWebSocketFrame) {
                TextWebSocketFrame msgText = (TextWebSocketFrame) msg;
                m.put("message", msgText.text());
                m.put("netType", "webSocket");
                m.put("event", "consume");
                log.info(JSON.toJSONString(m));
                kafkaProducer.send(WEB_SOCKET_TOPIC, JSON.toJSONString(m));
                this.okReplyWebSocket(ctx);
            }
        }, Throwable::printStackTrace);

        publish.subscribe(m -> {
            if (!(msg instanceof TextWebSocketFrame)) {
                ByteBuf buff = (ByteBuf) msg;
                String socketInfo = buff.toString(CharsetUtil.UTF_8).trim();
                m.put("message", socketInfo);
                m.put("netType", "socket");
                m.put("event", "consume");
                log.info(JSON.toJSONString(m));
                kafkaProducer.send(SOCKET_TOPIC, JSON.toJSONString(m));
                this.okReplySocket(ctx);
            }
        }, Throwable::printStackTrace);

    }

    /**
     * @author ：qiDing
     * @date ：Created in 2021/01/03 09:35
     * description：TODO WebSocket 回复
     */
    public void okReplyWebSocket(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new TextWebSocketFrame(new ResponseResult<>(20000).toJsonString()))
                .addListeners((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        future.cause().printStackTrace();
                        log.error("***IO错误,消息发送失败,关闭通道***");
                        future.channel().close();
                    }
                });
    }

    /**
     * @author ：qiDing
     * @date ：Created in 2021/01/03 10:37
     * description：TODO socket 回复
     */
    public void okReplySocket(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.copiedBuffer(new ResponseResult<>("请求成功").getBytes()))
                .addListeners((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        future.cause().printStackTrace();
                        log.error("***IO错误,消息发送失败,关闭通道***");
                        future.channel().close();
                    }
                });
    }

    /**
     * @author ：qiDing
     * @date ：Created in 2021/01/03 09:32
     * description：TODO 服务端给客户端发送消息
     */
    public void channelWrite(ChannelId channelId, Object msg) throws Exception {
        ChannelHandlerContext ctx = ConcurrentContext.CHANNEL_MAP.get(channelId);
        if (ctx == null) {
            log.info("通道【" + channelId + "】不存在");
            return;
        }
        ctx.write(msg);
        ctx.flush();
    }

    /**
     * @author ：qiDing
     * @date ：Created in 2021/01/02 11:47
     * description：TODO  超时管理
     */
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
}

