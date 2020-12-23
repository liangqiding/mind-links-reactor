package com.mind.links.mqtt.handler;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;



/**
 * @author qiding
 */
@Component
public class MqttClientPub {
    private static final Logger logger = LoggerFactory.getLogger(MqttClientPub.class);


    @Value("${mqtt.server.host}")
    private String broker;
    private final String CLIENT_ID = "i2dspSuperAdminMQTTClientIDPubWebEventLv1";
    private static final String UUID_MQTT = UUID.randomUUID().toString().replaceAll("-", "").substring(5);
    private static String IP;

    static {
        try {
            IP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private MqttClient mqttClient;

    private final MemoryPersistence memoryPersistence = new MemoryPersistence();


    /**
     * mqttClient连接服务器
     */
    static int C = 1;

    public void connectToServer() {
        try {
            String id = CLIENT_ID + "-" + UUID_MQTT + "-" + IP;
            mqttClient = new MqttClient(broker, id, memoryPersistence);
            mqttClient.setCallback(new MqttClientCallback() {
                @Override
                public void connectComplete(boolean b, String s) {
                    //连接完成，自动订阅--自动重连时。
                    logger.error("自动重连," + broker + ",次数：" + C++);
                }

                @Override
                public void connectionLost(Throwable throwable) {
                    logger.error(CLIENT_ID + "connection lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            logger.info("Connecting to broker :" + broker);
            mqttClient.connect(getMqttConnectionOptions());
            logger.info("Connected");
        } catch (MqttException e) {
            logger.error("reason" + e.getReasonCode());
            logger.error("msg" + e.getMessage());
            logger.error("loc" + e.getLocalizedMessage());
            logger.error("cause" + e.getCause());
            e.printStackTrace();
        }
    }

    /**
     * 发布系统消息
     */
    static int cont = 1;

    private synchronized void count() {
        cont++;
        logger.info("MQTT服务: " + mqttClient.getClientId() + "开始发送>>>>累计" + cont + "条");
    }

    public void mqttClientPublish(String topic, JSONArray message) {
        int qos = 2;
        MqttMessage mqttMessage = new MqttMessage(JSON.toJSONString(message).getBytes());
        mqttMessage.setQos(qos);
        try {
            mqttClient.publish(topic, mqttMessage);
            count();
            logger.info("===MQTT发送成功===事件topic:+" + topic);
        } catch (MqttException e) {
            logger.error("========================" + mqttClient.getClientId() + ",mqtt发送异常,准备打印异常：=======================================");
            logger.error(e.getMessage());
            logger.error("=========================================================END=========================================================");
        }
    }

    private MqttConnectOptions getMqttConnectionOptions() {
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(true);
        connectOptions.setUserName(CLIENT_ID);
        connectOptions.setUserName(mqttUserName);
        connectOptions.setPassword(mqttPassWord.toCharArray());
        connectOptions.setAutomaticReconnect(true);

        // 最大并发发送条数 10万条
        connectOptions.setMaxInflight(100000);

        // 设置超时时间
        connectOptions.setConnectionTimeout(30);

        // 设置会话心跳时间
        connectOptions.setKeepAliveInterval(20);

        return connectOptions;
    }

    @Value("${mqtt.client.username}")
    private String mqttUserName;

    @Value("${mqtt.client.password}")
    private String mqttPassWord;
}
