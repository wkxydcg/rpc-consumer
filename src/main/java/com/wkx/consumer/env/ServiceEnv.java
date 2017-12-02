package com.wkx.consumer.env;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;


public class ServiceEnv implements ApplicationContextInitializer<ConfigurableApplicationContext>{

    private static Environment environment;

    public static String getProperty(String key){
        return environment.getProperty(key);
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        environment=applicationContext.getEnvironment();
    }
}
