package com.mind.links.netty.mqtt.mqttStore.publish;

import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;

/**
 * PUBLISH重发消息存储
 *
 * @author qiding
 */
@Data
@Accessors(chain = true)
public class DupPublishMessageStore implements Serializable {

	private static final long serialVersionUID = -8112511377194421600L;

	private String clientId;

	private String topic;

	private int mqttQoS;

	private int messageId;

	private byte[] messageBytes;


}
