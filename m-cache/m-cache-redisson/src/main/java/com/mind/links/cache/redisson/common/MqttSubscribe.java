package com.mind.links.cache.redisson.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 订阅存储 订阅实体类
 * @author qiding
 */
@Data
@Accessors(chain = true)
public class MqttSubscribe implements Serializable {

	private static final long serialVersionUID = -1;


	private String clientId;


	private String topic;

	private Integer mqttQoS;

	public MqttSubscribe(String clientId, String topic, int mqttQoS) {
		this.clientId = clientId;
		this.topic = topic;
		this.mqttQoS = mqttQoS;
	}
	public MqttSubscribe(){

	}

}
