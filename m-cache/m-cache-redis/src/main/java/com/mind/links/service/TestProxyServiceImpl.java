package com.mind.links.service;

import com.mind.links.proxy.JdkDynamicProxy;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2017-12-11 08:30
 * @version v1.0.0
 */
public class TestProxyServiceImpl implements TestProxyService {

    @Override
    public void test() {
        System.out.println("=============test执行了===================");
    }

    public static void main(String[] args) {
        JdkDynamicProxy jdkDynamicProxy = new JdkDynamicProxy(new TestProxyServiceImpl());
        TestProxyService proxy = jdkDynamicProxy.getProxy();
        proxy.test();
    }
}
