package com.mind.links.mqtt.handler;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;



/**
 * @author qiding
 */
@Component
@Slf4j
@ApiModel("mqtt测试客户端(只测试)")
public class MqttClientPub {

    @Value("${mqtt.server.host}")
    private String broker;

    @Value("${mqtt.client.username}")
    private String mqttUserName;

    @Value("${mqtt.client.password}")
    private String mqttPassWord;

    @ApiModelProperty("客户端名")
    private final String CLIENT_ID = "mqttTest";

    @ApiModelProperty("客户端随机id")
    private static final String UUID_MQTT = UUID.randomUUID().toString().replaceAll("-", "").substring(5);

    @ApiModelProperty("服务器IP")
    private static String IP;

    private final MemoryPersistence memoryPersistence = new MemoryPersistence();

    static {
        try {
            IP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private MqttClient mqttClient;



    /**
     * mqttClient连接服务器
     */
    public void connectToServer() {
        try {
            String id = CLIENT_ID + "-" + UUID_MQTT + "-" + IP;
            mqttClient = new MqttClient(broker, id, memoryPersistence);
            mqttClient.setCallback(new MqttClientCallback() {
                @Override
                public void connectComplete(boolean b, String s) {
                    log.error("自动重连," + broker );
                }
                @Override
                public void connectionLost(Throwable throwable) {
                    log.error(CLIENT_ID + "connection lost");
                }
                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }
            });
            log.info("Connecting to broker :" + broker);
            mqttClient.connect(getMqttConnectionOptions());
            log.info("Connected");
        } catch (MqttException e) {
            log.error("reason" + e.getReasonCode());
            log.error("msg" + e.getMessage());
            log.error("loc" + e.getLocalizedMessage());
            log.error("cause" + e.getCause());
            e.printStackTrace();
        }
    }

    /**
     * 发布系统消息
     */
    public void mqttClientPublish(String topic, JSONArray message) {
        int qos = 2;
        MqttMessage mqttMessage = new MqttMessage(JSON.toJSONString(message).getBytes());
        mqttMessage.setQos(qos);
        try {
            mqttClient.publish(topic, mqttMessage);
            log.info("===发送事件topic:+" + topic);
        } catch (MqttException e) {
            log.error("***" + mqttClient.getClientId() + ","+e.getMessage());

        }
    }

    private MqttConnectOptions getMqttConnectionOptions() {
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(true);
        connectOptions.setUserName(CLIENT_ID);
        connectOptions.setUserName(mqttUserName);
        connectOptions.setPassword(mqttPassWord.toCharArray());
        connectOptions.setAutomaticReconnect(true);
        // 最大并发发送条数 1万条
        connectOptions.setMaxInflight(10000);
        // 设置超时时间
        connectOptions.setConnectionTimeout(30);
        // 设置会话心跳时间
        connectOptions.setKeepAliveInterval(20);
        return connectOptions;
    }
}
