package com.mind.links.netty;

import com.mind.links.netty.nettyConfig.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-21 13:40
 * @version v1.0.0
 */
@SpringBootApplication
public class NettyLinksApplication implements ApplicationRunner {

    @Autowired
    NettyServer nettyServer;

    public static void main(String[] args) {
        SpringApplication.run(NettyLinksApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        nettyServer.start();
    }

}
