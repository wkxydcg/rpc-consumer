package com.wkx.consumer.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ServiceEnv implements EnvironmentAware{

    private static Environment environment;

    @Override
    public void setEnvironment(Environment ev) {
        environment=ev;
    }

    public static String getProperty(String key){
        return environment.getProperty(key);
    }
}
