package com.mind.links.logger.handler.config;

import com.mind.links.logger.handler.aopLog.CustomAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
@ConditionalOnClass(CustomAspect.class)
@ComponentScan(basePackages = {"com.mind.links.logger.handler.aopLog"})
public class LinksCommonClientConfig {

}

