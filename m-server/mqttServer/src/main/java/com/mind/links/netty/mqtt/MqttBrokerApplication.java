package com.mind.links.netty.mqtt;

import com.mind.links.netty.mqtt.mqttServer.MqttBrokerServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * description : 启动mqtt服务器
 *
 * @author : qiDing
 * date: 2020-12-21 13:40
 * @version v1.0.0
 */
@SpringBootApplication
public class MqttBrokerApplication implements ApplicationRunner {

    @Autowired
    MqttBrokerServer mqttBrokerServer;

    public static void main(String[] args) {
        SpringApplication.run(MqttBrokerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        mqttBrokerServer.start();
    }

}
