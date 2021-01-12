package com.mind.links.netty.mqtt.mqttStore.cache;


import com.mind.links.netty.mqtt.mqttStore.SubscribeStore;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by wizzer on 2018
 */
@Component
public class SubscribeNotWildcardCache {

    public ConcurrentHashMap<String, SubscribeStore> subscribeStoreMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, String> topicMap = new ConcurrentHashMap<>();

    public SubscribeStore put(String topic, String clientId, SubscribeStore subscribeStore) {
        subscribeStoreMap.put(topic + clientId, subscribeStore);
        topicMap.put(clientId, topic);
        return subscribeStore;
    }

    public SubscribeStore get(String topic, String clientId) {
        return subscribeStoreMap.get(topic + clientId);
    }

    public boolean containsKey(String topic, String clientId) {
        return subscribeStoreMap.containsKey(topic + clientId);
    }

    public void remove(String topic, String clientId) {
        subscribeStoreMap.remove(topic + clientId);
        topicMap.remove(clientId);
    }

    public void removeForClient(String clientId) {
//        subscribeStoreMap.remove(clientId);
        topicMap.remove(clientId);
    }

    public Map<String, ConcurrentHashMap<String, SubscribeStore>> all() {
        HashMap<String, ConcurrentHashMap<String, SubscribeStore>> map = new HashMap<>();
        map.put("all", subscribeStoreMap);
        return map;
    }

    public List<SubscribeStore> all(String topic) {
        return subscribeStoreMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }
}
