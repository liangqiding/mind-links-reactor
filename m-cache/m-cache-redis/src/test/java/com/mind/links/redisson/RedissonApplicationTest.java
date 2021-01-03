package com.mind.links.redisson;

import com.mind.links.redisson.proxy.JdkDynamicProxy;
import org.junit.jupiter.api.Test;

/**
 * @author qiDing
 * date: 2021-01-03 16:22
 * @version v1.0.0
 * description
 */

public class RedissonApplicationTest {

    @Test
    public void one() {
        JdkDynamicProxy jdkDynamicProxy = new JdkDynamicProxy(new TestProxyServiceImpl());
        TestProxyService proxy = jdkDynamicProxy.getProxy();
        proxy.test();
    }

}
