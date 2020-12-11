package com.mind.links.common.utils;


import org.springframework.stereotype.Component;


/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-11 13:11
 * @version v1.0.0
 */
@Component
public class SnowflakeComponent {



    private static volatile SnowflakeUtils instance;

    public SnowflakeUtils getInstance() {
        if (instance == null) {
            synchronized (SnowflakeUtils.class) {
                if (instance == null) {
                    instance = new SnowflakeUtils();
                }
            }
        }
        return instance;
    }
}
