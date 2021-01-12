
package com.mind.links.netty.nettyMqtt.COMMON.session;

import org.nutz.aop.interceptor.async.Async;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话存储服务
 * @author qidingliang
 */
@Component
public class SessionStoreServiceImpl implements ISessionStoreService {

    public ConcurrentHashMap<String, SessionStore> sessionStoreMap = new ConcurrentHashMap<>();

    @Override
    public void put(String clientId, SessionStore sessionStore, int expire) {
        //SessionStore对象不能正常转为JSON,使用工具类类解决
        if (!sessionStoreMap.containsKey(clientId)) {
            sessionStoreMap.put(clientId, sessionStore);
        }
    }

    @Override
    public void expire(String clientId, int expire) {
        if (!sessionStoreMap.containsKey(clientId)) {
            sessionStoreMap.remove(clientId);
        }
    }


    @Override
    public SessionStore get(String clientId) {
        if (!sessionStoreMap.containsKey(clientId)) {
            return sessionStoreMap.get(clientId);
        }
        return null;
    }

    @Override
    public boolean containsKey(String clientId) {
        return sessionStoreMap.containsKey(clientId);
    }


    @Override
    @Async
    public void remove(String clientId) {
        sessionStoreMap.remove(clientId);
    }

}
