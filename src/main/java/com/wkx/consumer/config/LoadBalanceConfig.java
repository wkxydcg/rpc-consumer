package com.wkx.consumer.config;

import com.wkx.consumer.loadBalance.LoadBalance;
import com.wkx.consumer.loadBalance.RotationLoadBalance;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadBalanceConfig {

    @ConditionalOnMissingBean(LoadBalance.class)
    @Bean
    public LoadBalance getLoadBalance(){
        return new RotationLoadBalance();
    }

}
