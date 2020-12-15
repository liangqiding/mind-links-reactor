package com.mind.links.logger.handler.aopLog;

import java.lang.annotation.*;

/**
 * @author : qiDing
 * Date: 2020-11-08 12:46
 * @version v1.0.0
 * Description : TODO Custom aop operation annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
@Documented
public @interface CustomAopHandler {

    /**
     * log ## Universal log printing in any scenario
     */
    boolean log() default true;

    /**
     * checkPage ## Paging parameter validation ( pageNumber pageSize )
     */
    boolean checkPage() default false;

    /**
     * module
     */
    String module() default "";

    /**
     * desc
     */
    String desc() default "";

}
