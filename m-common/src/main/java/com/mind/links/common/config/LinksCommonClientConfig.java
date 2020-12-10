package com.mind.links.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * description : TODO  装载bean
 *
 * @author : qiDing
 * date: 2020-12-09 17:42
 * @version v1.0.0
 */
@Configuration
@ComponentScan("com.mind.links.common.aopLog.**")
public class LinksCommonClientConfig {

}

