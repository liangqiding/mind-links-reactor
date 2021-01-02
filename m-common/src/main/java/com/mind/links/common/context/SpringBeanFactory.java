package com.mind.links.common.context;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * @author qiding
 */
@Component
public final class SpringBeanFactory implements ApplicationContextAware {

    @ApiModelProperty("spring上下文")
    private static ApplicationContext CONTENT;

    public static <T> T getBean(Class<T> c) {
        return CONTENT.getBean(c);
    }


    public static <T> T getBean(String name, Class<T> clazz) {
        return CONTENT.getBean(name, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) CONTENT.getBean(name);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        CONTENT = applicationContext;
    }

    /**
     * 注册Bean
     */
    public void registerBean(Class<?> cl) {
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) SpringBeanFactory.CONTENT;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(cl);
        beanFactory.registerBeanDefinition(cl.getName(), beanDefinitionBuilder.getBeanDefinition());
    }
}
