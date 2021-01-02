package com.mind.links.netty.uitls;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author qiding
 */
@Component
public final class SpringBeanFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    public static <T> T getBean(Class<T> c) {
        return context.getBean(c);
    }


    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) context.getBean(name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

//    /**
//     * 注册Bean
//     */
//    public void registerBean(Class<?> cl) {
//        ConfigurableApplicationContext context = (ConfigurableApplicationContext) SpringBeanFactory.context;
//        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
//        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(cl);
//        beanFactory.registerBeanDefinition(cl.getName(), beanDefinitionBuilder.getBeanDefinition());
//    }
}
