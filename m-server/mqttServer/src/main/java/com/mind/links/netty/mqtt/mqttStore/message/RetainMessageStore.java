package com.mind.links.netty.mqtt.mqttStore.message;

import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;

/**
 * Retain标志消息存储
 * @author qiding
 */
@Data
@Accessors(chain = true)
public class RetainMessageStore implements Serializable {

	private static final long serialVersionUID = -7548204047370972779L;

	private String topic;

	private byte[] messageBytes;

	private int mqttQoS;
}
