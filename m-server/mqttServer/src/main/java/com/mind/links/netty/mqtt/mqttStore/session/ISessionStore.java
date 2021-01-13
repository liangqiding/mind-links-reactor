package com.mind.links.netty.mqtt.mqttStore.session;

import com.mind.links.netty.mqtt.mqttStore.MqttSession;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import org.apache.ibatis.annotations.Param;

/**
 * 会话存储服务接口
 * @author qiding
 */
public interface ISessionStore {

    /**
     * 清空缓存会话 如果会话中已存储这个新连接的clientId, 就关闭之前该clientId的连接
     *
     * @param msg Mqtt连接消息
     */
    void cleanSession(MqttConnectMessage msg);


    /**
     * 缓存会话
     *
     * @param clientId 客户端id
     * @param mqttSession  session
     * @param expire 有效期
     */
    void put(String clientId, MqttSession mqttSession, int expire);

    /**
     * 设置会话失效时间
     *
     * @param clientId 客户端id
     * @param expire 有效期
     */
    void expire(String clientId, int expire);

    /**
     * 获取会话
     *
     * @param clientId 客户端id
     */
    MqttSession get(@Param("clientId") String clientId);

    /**
     * 判断会话是否存在
     *
     * @param clientId clientId的会话是否存在
     * @return true or false
     */
    boolean containsKey(String clientId);


    /**
     * 删除会话
     *
     * @param clientId clientId
     */
    void remove(String clientId);

}
