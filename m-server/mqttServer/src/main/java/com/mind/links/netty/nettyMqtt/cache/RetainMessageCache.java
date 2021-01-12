package com.mind.links.netty.nettyMqtt.cache;


import com.alibaba.fastjson.JSONObject;
import com.mind.links.netty.nettyMqtt.COMMON.message.RetainMessageStore;
import com.mind.links.netty.nettyMqtt.COMMON.subscribe.SubscribeStore;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.lang.Streams;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by wizzer on 2018
 */
@Component
public class RetainMessageCache {

    public ConcurrentHashMap<String, RetainMessageStore> retainMessageStoreMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, String> topicMap = new ConcurrentHashMap<>();

    public RetainMessageStore put(String topic, RetainMessageStore obj) {
        retainMessageStoreMap.put(topic, obj);
        return obj;
    }

    public RetainMessageStore get(String topic) {
        return retainMessageStoreMap.get(topic);
    }

    public boolean containsKey(String topic) {
        return retainMessageStoreMap.containsKey(topic);
    }

    @Async
    public void remove(String topic) {
        retainMessageStoreMap.remove(topic);
    }

    public Map<String, RetainMessageStore> all() {

        return retainMessageStoreMap;
    }
}
