package com.mind.links.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-10 14:27
 * @version v1.0.0
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient getRedisson() throws Exception {
        RedissonClient redisson = null;
        Config config = new Config();
        // 本例子使用的是yaml格式的配置文件，读取使用Config.fromYAML，如果是Json文件，则使用Config.fromJSON
        config = Config.fromYAML(RedissonConfig.class.getClassLoader().getResource("redisson-config.yml"));
        config.useSingleServer().setDatabase(13);
        redisson = Redisson.create(config);
        return redisson;
    }
}
