package com.mind.links.netty.mqtt.mqttServer;

import javax.annotation.PreDestroy;

/**
 * @author qiding
 */
public interface IMqttServer {


    /**
     * 主启动程序，初始化参数
     *
     * @throws Exception 初始化异常
     */
    void start() throws Exception;


    /**
     * mqtt服务器 核心配置
     *
     * @throws Exception 启动异常
     */
    void mqttServer() throws Exception;

    /**
     * 优雅的结束服务器
     */
    @PreDestroy
    void destroy();
}
