package com.mind.links.netty.nettyInitializer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;

/**
 * @author : qiDing
 * date: 2021-01-02 10:13
 * @version v1.0.0
 */
@Component
@ApiModel("webSocket解码器")
public class WebSocketPipeline {

    public void webSocketPipelineAdd(ChannelHandlerContext ctx) {
        WebSocketPipelineProperties wp = new WebSocketPipelineProperties();
        ctx.pipeline().addBefore(wp.HANDLER_NAME, wp.httpServerCodec.getClass().getSimpleName(), wp.httpServerCodec)
                .addBefore(wp.HANDLER_NAME, wp.chunkedWriteHandler.getClass().getSimpleName(), wp.chunkedWriteHandler)
                .addBefore(wp.HANDLER_NAME, wp.httpObjectAggregator.getClass().getSimpleName(), wp.httpObjectAggregator)
                .addBefore(wp.HANDLER_NAME, wp.webSocketFrameAggregator.getClass().getSimpleName(), wp.webSocketFrameAggregator)
                .addBefore(wp.HANDLER_NAME, wp.webSocketServerProtocolHandler.getClass().getSimpleName(), wp.webSocketServerProtocolHandler);
    }

    @ApiModel("webSocket解码器配置(多例)")
    public static class WebSocketPipelineProperties {

        @ApiModelProperty("http解码器")
        volatile HttpServerCodec httpServerCodec = new HttpServerCodec();

        @ApiModelProperty("http聚合器")
        volatile ChunkedWriteHandler chunkedWriteHandler = new ChunkedWriteHandler();

        @ApiModelProperty("http消息聚合器")
        volatile HttpObjectAggregator httpObjectAggregator = new HttpObjectAggregator(1024 * 62);

        @ApiModelProperty("webSocket消息聚合器")
        volatile WebSocketFrameAggregator webSocketFrameAggregator = new WebSocketFrameAggregator(1024 * 62);

        @ApiModelProperty("webSocket支持,设置路由")
        volatile WebSocketServerProtocolHandler webSocketServerProtocolHandler = new WebSocketServerProtocolHandler("/ws");

        @ApiModelProperty("netty消息处理类")
        private final String HANDLER_NAME = "NettyServerHandler";

    }
}
