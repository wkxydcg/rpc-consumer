package com.wkx.consumer.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class BeanFactory implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static ApplicationContext context;

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static <T> T getBean(Class<T> c) {
        return context.getBean(c);
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        context = applicationContext;
    }
}
