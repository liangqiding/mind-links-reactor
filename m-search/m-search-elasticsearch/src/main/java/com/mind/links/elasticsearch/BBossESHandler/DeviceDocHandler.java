package com.mind.links.elasticsearch.BBossESHandler;

import lombok.extern.slf4j.Slf4j;
import org.frameworkset.elasticsearch.ElasticSearchException;
import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : qiDing
 * date: 2020-11-14 10:03
 * @version v1.0.0
 */
@Component
@Slf4j
public class DeviceDocHandler {
    @Autowired
    private BBossESStarter esStarter;

    private final String MAPPER_PATH = "es/mapper/device.xml";

    public void createDeviceIndex() {
        //创建加载配置文件的客户端工具，单实例多线程安全
        ClientInterface clientUtil = esStarter.getConfigRestClient(MAPPER_PATH);
        try {
            //判读索引表device是否存在，存在返回true，不存在返回false
            boolean exist = clientUtil.existIndice("device");

            //如果索引表device已经存在先删除mapping
            if (exist) {
                String r = clientUtil.dropIndice("device");
                log.info("===clientUtil.dropIndice(\"demo\") response:" + r);
                //获取最新建立的索引表结构
                String demoIndex = clientUtil.getIndice("device");
                log.info("===获取最新建立的索引表结构==" + demoIndex);
            }
            //创建索引表device (索引表名称,索引表mapping dsl脚本名称，在esMapper/demo.xml中定义createDemoIndex)
            clientUtil.createIndiceMapping("device",
                    "createDeviceIndex");
            //获取最新建立的索引表结构
            String demoIndex = clientUtil.getIndice("device");
            log.info("===获取最新建立的索引表结构==" + demoIndex);
        } catch (ElasticSearchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
