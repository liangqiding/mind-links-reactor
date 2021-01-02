package com.mind.links.netty.nettyConfig;

import com.mind.links.netty.nettyHandler.ChannelActiveHandler;
import com.mind.links.netty.nettyHandler.ChannelInactiveHandler;
import com.mind.links.netty.nettyHandler.NettyServerHandler;
import com.mind.links.netty.nettyHandler.SocketChooseHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2021-01-02 10:13
 * @version v1.0.0
 */
@Component
@ApiModel("webSocket定制解码器")
public class WebSocketPipeline {

    @ApiModelProperty("http解码器")
    volatile HttpServerCodec httpServerCodec;

    @ApiModelProperty("http聚合器")
    volatile ChunkedWriteHandler chunkedWriteHandler;

    @ApiModelProperty("http消息聚合器")
    volatile HttpObjectAggregator httpObjectAggregator;

    @ApiModelProperty("webSocket消息聚合器")
    volatile WebSocketFrameAggregator webSocketFrameAggregator;

    @ApiModelProperty("webSocket支持,设置路由")
    volatile WebSocketServerProtocolHandler webSocketServerProtocolHandler;

    @ApiModelProperty("netty消息处理类")
    private final String HANDLER_NAME = "NettyServerHandler";


    public void webSocketPipelineAdd(ChannelHandlerContext ctx) {

        this.init();

        ctx.pipeline().addBefore(HANDLER_NAME, httpServerCodec.getClass().getSimpleName(), httpServerCodec);

        ctx.pipeline().addBefore(HANDLER_NAME, chunkedWriteHandler.getClass().getSimpleName(), chunkedWriteHandler);

        ctx.pipeline().addBefore(HANDLER_NAME, httpObjectAggregator.getClass().getSimpleName(), httpObjectAggregator);

        ctx.pipeline().addBefore(HANDLER_NAME, webSocketFrameAggregator.getClass().getSimpleName(), webSocketFrameAggregator);

        ctx.pipeline().addBefore(HANDLER_NAME, webSocketServerProtocolHandler.getClass().getSimpleName(), webSocketServerProtocolHandler);

    }

    /**
     * @author ：qiDing
     * @date ：Created in 2021/01/02 16:55
     * description：TODO 排除非单例注入
     */
    private void init() {
        this.httpServerCodec = new HttpServerCodec();
        this.chunkedWriteHandler = new ChunkedWriteHandler();
        this.httpObjectAggregator = new HttpObjectAggregator(1024 * 62);
        this.webSocketFrameAggregator = new WebSocketFrameAggregator(1024 * 62);
        this.webSocketServerProtocolHandler = new WebSocketServerProtocolHandler("/ws");
    }
}
