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
public class LinksSnowflakeComponent {



    private static volatile LinksSnowflakeUtils instance;

    public LinksSnowflakeUtils getInstance() {
        if (instance == null) {
            synchronized (LinksSnowflakeUtils.class) {
                if (instance == null) {
                    instance = new LinksSnowflakeUtils();
                }
            }
        }
        return instance;
    }
}
