package com.mind.links.netty.mqtt.common;


import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;

/**
 * 会话存储
 *
 * @author qidingliang
 */
@Data
@Accessors(chain = true)
@ApiModel("会话存储实体类")
public class MqttSession implements Serializable {
    private static final long serialVersionUID = -1L;

    @ApiModelProperty("服务器id")
    private String brokerId;

    @ApiModelProperty("客户端id")
    private String clientId;

    @ApiModelProperty("管道id")
    private String channelId;

    @ApiModelProperty("到期时间")
    private Integer expire;

    @ApiModelProperty("清空session")
    private boolean cleanSession;

    @ApiModelProperty("Mqtt发布消息")
    private MqttPublishMessage willMessage;

}
