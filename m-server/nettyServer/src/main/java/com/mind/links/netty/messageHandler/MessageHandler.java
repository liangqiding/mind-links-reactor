package com.mind.links.netty.messageHandler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import reactor.core.publisher.Mono;
import java.util.HashMap;

/**
 * @author qiDing
 * date: 2021-01-05 08:31
 * @version v1.0.0
 * description
 */

public interface MessageHandler {

    /**
     * socket 协议消息处理
     *
     * @param hashMap 通道消息
     * @param ctx     ctx
     * @param msg     消息体
     * @return hashMap
     */
    Mono<HashMap<String, Object>> socketHandler(HashMap<String, Object> hashMap, ChannelHandlerContext ctx, Object msg);

    /**
     * WebSocket 协议消息处理
     *
     * @param hashMap 通道消息
     * @param ctx     ctx
     * @param msg     消息体
     * @return hashMap
     */
    Mono<HashMap<String, Object>> webSocketHandler(HashMap<String, Object> hashMap, ChannelHandlerContext ctx, Object msg);


    /**
     * WebSocket 回复
     *
     * @param ctx ChannelHandlerContext
     */
    void okReplyWebSocket(ChannelHandlerContext ctx);


    /**
     * socket 回复
     *
     * @param ctx ChannelHandlerContext
     */
    void okReplySocket(ChannelHandlerContext ctx);


    /**
     * 服务端给客户端发送消息
     *
     * @param channelId 管道id
     * @param msg       消息体
     * @throws Exception io
     */
    void channelWrite(ChannelId channelId, Object msg) throws Exception;
}
