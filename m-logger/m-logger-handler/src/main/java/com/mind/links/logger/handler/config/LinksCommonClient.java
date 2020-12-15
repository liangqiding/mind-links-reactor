package com.mind.links.logger.handler.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * description : TODO  供调用者装载bean
 *
 * @author : qiDing
 * date: 2020-12-09 17:42
 * @version v1.0.0
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({LinksCommonClientConfig.class})
public @interface LinksCommonClient {

}
