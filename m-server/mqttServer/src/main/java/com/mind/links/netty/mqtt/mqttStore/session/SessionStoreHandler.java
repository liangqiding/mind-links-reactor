package com.mind.links.netty.mqtt.mqttStore.session;

import com.alibaba.fastjson.JSON;
import com.mind.links.netty.mqtt.common.ChannelCommon;
import com.mind.links.netty.mqtt.config.BrokerProperties;
import com.mind.links.netty.mqtt.mqttStore.MqttSession;
import com.mind.links.netty.mqtt.mqttServer.MqttBrokerServer;
import com.mind.links.netty.mqtt.mqttStore.subscribe.SubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话存储服务
 *
 * @author qiding
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SessionStoreHandler implements ISessionStore{

    private final SubscribeStoreService subscribeStoreService;

    private final BrokerProperties brokerProperties;

    public ConcurrentHashMap<String, MqttSession> sessionStoreMap = new ConcurrentHashMap<>();

    public void extendSession(Channel channel) {
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        if (containsKey(clientId)) {
            MqttSession sessionStore = get(clientId);
            ChannelId channelId = ChannelCommon.channelIdMap.get(sessionStore.getBrokerId() + "_" + sessionStore.getChannelId());
            if (brokerProperties.getId().equals(sessionStore.getBrokerId()) && channelId != null) {
                expire(clientId, sessionStore.getExpire());
            }
        }
     }

    @Override
    public void cleanSession(MqttConnectMessage msg) {
        if (this.containsKey(msg.payload().clientIdentifier())) {
            MqttSession sessionStore = this.get(msg.payload().clientIdentifier());
            boolean cleanSession = sessionStore.isCleanSession();
            if (cleanSession) {
                // 清理缓存
            }
            try {
                ChannelId channelId = ChannelCommon.channelIdMap.get(sessionStore.getBrokerId() + "_" + sessionStore.getChannelId());
                if (channelId != null) {
                    Channel previous = ChannelCommon.channelGroup.find(channelId);
                    if (previous != null) previous.close();
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        } else {
            //如果不存在session，则清除之前的其他缓存
            subscribeStoreService.removeForClient(msg.payload().clientIdentifier());
        }
     }


    @Override
    public void put(String clientId, MqttSession sessionStore, int expire) {
        //SessionStore对象不能正常转为JSON,使用工具类类解决
        log.debug("保存session:"+ JSON.toJSONString(sessionStore));
        if (!sessionStoreMap.containsKey(clientId)) {
            sessionStoreMap.put(clientId, sessionStore);
        }
    }

    @Override
    public void expire(String clientId, int expire) {
        if (!sessionStoreMap.containsKey(clientId)) {
            // 设置失效时间
        }
    }


    @Override
    public MqttSession get(String clientId) {
        if (sessionStoreMap.containsKey(clientId)) {
            return sessionStoreMap.get(clientId);
        }
        return null;
    }

    @Override
    public boolean containsKey(String clientId) {
        return sessionStoreMap.containsKey(clientId);
    }


    @Override
    public void remove(String clientId) {
        sessionStoreMap.remove(clientId);
    }

}
