package com.mind.links.netty.messageHandler;

import com.alibaba.fastjson.JSON;
import com.mind.links.common.response.ResponseResult;
import com.mind.links.netty.common.ConcurrentContext;
import com.mind.links.netty.kafka.KafkaProducers;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * @author qiDing
 * date: 2021-01-05 08:31
 * @version v1.0.0
 * description
 */
@Slf4j
@Service
public class MessageHandlerImpl implements MessageHandler {

    @ApiModelProperty("socket TOPIC")
    private static final String SOCKET_TOPIC = "socket";

    @ApiModelProperty("webSocket TOPIC")
    private static final String WEB_SOCKET_TOPIC = "webSocket";

    private static KafkaProducers kafkaProducer;

    @Autowired
    public void setKafkaProducer(KafkaProducers k) {
        kafkaProducer = k;
    }


    @Override
    public Mono<HashMap<String, Object>> socketHandler(HashMap<String, Object> hashMap, ChannelHandlerContext ctx, Object msg) {
        return Mono.just(hashMap)
                .flatMap(o -> Mono.just(hashMap)
                        .map(msgMap -> {
                            if (!(msg instanceof TextWebSocketFrame)){
                                ByteBuf buff = (ByteBuf) msg;
                                String socketInfo = buff.toString(CharsetUtil.UTF_8).trim();
                                msgMap.put("message", socketInfo);
                                msgMap.put("netType", "socket");
                                msgMap.put("event", "consume");
                                kafkaProducer.send(SOCKET_TOPIC, JSON.toJSONString(msgMap));
                                okReplySocket(ctx);
                            }
                            return msgMap;
                        }));
    }

    @Override
    public Mono<HashMap<String, Object>> webSocketHandler(HashMap<String, Object> hashMap, ChannelHandlerContext ctx, Object msg) {
        return Mono.just(hashMap)
                .flatMap(o -> Mono.just(hashMap)
                        .map(msgMap -> {
                            if (msg instanceof TextWebSocketFrame){
                                TextWebSocketFrame msgText = (TextWebSocketFrame) msg;
                                msgMap.put("message", msgText.text());
                                msgMap.put("netType", "webSocket");
                                msgMap.put("event", "consume");
                                okReplyWebSocket(ctx);
                                kafkaProducer.send(WEB_SOCKET_TOPIC, JSON.toJSONString(msgMap));
                            }
                            return msgMap;
                        }));
    }


    @Override
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


    @Override
    public void okReplySocket(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.copiedBuffer(new ResponseResult<>("请求成功").toBytes()))
                .addListeners((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        future.cause().printStackTrace();
                        log.error("***IO错误,消息发送失败,关闭通道***");
                        future.channel().close();
                    }
                });
    }


    @Override
    public void channelWrite(ChannelId channelId, Object msg) throws Exception {
        ChannelHandlerContext ctx = ConcurrentContext.CHANNEL_MAP.get(channelId);
        if (ctx == null) {
            log.info("通道【" + channelId + "】不存在");
            return;
        }
        ctx.write(msg);
        ctx.flush();
    }
}
