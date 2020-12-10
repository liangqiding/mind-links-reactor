package com.mind.links.common.config;

import java.util.ArrayList;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-09 16:15
 * @version v1.0.0
 */
public class LinkCommonClient {

    private final ArrayList<Class<?>> classes = new ArrayList<>();

    public static LinkCommonClient builder() {
        return new LinkCommonClient();
    }

    public LinkCommonClient add(String className) throws ClassNotFoundException {
        classes.add(getClass().getClassLoader().loadClass(className));
        return this;
    }


    public void register() {

    }
}
