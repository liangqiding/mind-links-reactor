package com.mind.links.netty.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.concurrent.ConcurrentHashMap;

/**
 * description : TODO  线程公共资源
 *
 * @author : qiDing
 * date: 2021-01-02 13:18
 * @version v1.0.0
 */
@ApiModel("并发安全的公共资源")
public class ConcurrentContext {

    @ApiModelProperty("管理一个全局map，保存连接进服务端的通道数量")
    public static final ConcurrentHashMap<ChannelId, ChannelHandlerContext> CHANNEL_MAP = new ConcurrentHashMap<>(2 << 5);

}
