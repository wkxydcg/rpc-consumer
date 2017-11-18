package com.wkx.consumer.controller;

import com.wkx.consumer.api.TestApi;
import com.wkx.consumer.config.BeanFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private TestApi testApi= BeanFactory.getBean(TestApi.class);

    @RequestMapping("/test")
    public String test(){
        return testApi.hello();
    }

}
