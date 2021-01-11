package com.mind.links.netty.common;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import reactor.core.publisher.Mono;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
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


    @ApiModelProperty("服务器地址")
    private static String address;

    @ApiModelProperty("服务器端口")
    private static String port;

    static {
        try {
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static Mono<HashMap<String, Object>> getMsgMap(ChannelHandlerContext ctx) {
        final HashMap<String, Object> msgMap = new HashMap<>(2 << 1);
        return Mono.just(ctx).map(ctx0 -> {
            final InetSocketAddress inSocket = (InetSocketAddress) ctx0.channel().remoteAddress();
            msgMap.put("channelId", ctx0.channel().id().asLongText());
            msgMap.put("clientIp", inSocket.getAddress().getHostAddress());
            msgMap.put("clientPort", inSocket.getPort());
            msgMap.put("servicePort", port);
            msgMap.put("serviceIp", address);
            msgMap.put("connected", ConcurrentContext.CHANNEL_MAP.size());
            return msgMap;
        });
    }

    public static Mono<String> getMsgMapToJsonString(ChannelHandlerContext ctx) {
        return getMsgMap(ctx).map(JSON::toJSONString);
    }
}
