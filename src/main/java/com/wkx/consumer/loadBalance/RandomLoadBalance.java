package com.wkx.consumer.loadBalance;

import com.wkx.consumer.config.ServiceMap;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomLoadBalance implements LoadBalance{

    public String getRequestUrl(String serviceId){
        Map<String,List<String>> map= ServiceMap.getServiceMap();
        if(StringUtils.isEmpty(serviceId)){
            throw new RuntimeException("serviceId 不可为空");
        }
        List<String> serviceList=map.get("/"+serviceId);
        if(serviceList==null||serviceList.size()==0){
            throw new RuntimeException("找不到相关服务");
        }
        int index=ThreadLocalRandom.current().nextInt(serviceList.size()-1);
        return "http://"+serviceList.get(index);
    }

}
