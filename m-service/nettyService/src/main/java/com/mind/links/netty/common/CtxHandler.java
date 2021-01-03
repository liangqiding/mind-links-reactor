package com.mind.links.netty.common;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.swagger.annotations.ApiModel;
import reactor.core.publisher.Mono;
import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * description : TODO   ctx 公共处理类
 *
 * @author : qiDing
 * date: 2021-01-02 15:33
 * @version v1.0.0
 */
@ApiModel("ctx公共处理类")
public class CtxHandler {

    public static Mono<HashMap<String, Object>> getMsgMap(ChannelHandlerContext ctx) {
        final HashMap<String, Object> msgMap = new HashMap<>(2 << 1);
        return Mono.just(ctx).map(ctx0 -> {
            final InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
            msgMap.put("channelId", ctx.channel().id().asLongText());
            msgMap.put("ip", inSocket.getAddress().getHostAddress());
            msgMap.put("port", inSocket.getPort());
            msgMap.put("connected", ConcurrentContext.CHANNEL_MAP.size());
            return msgMap;
        });
    }

    public static Mono<String> getMsgMapToJsonString(ChannelHandlerContext ctx) {
        return getMsgMap(ctx).map(JSON::toJSONString);
    }
}
