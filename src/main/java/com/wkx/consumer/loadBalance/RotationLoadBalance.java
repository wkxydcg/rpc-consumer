package com.wkx.consumer.loadBalance;

import com.wkx.consumer.config.ServiceMap;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Primary
public class RotationLoadBalance implements LoadBalance{

    private AtomicInteger requestNum=new AtomicInteger(0);

    public String getRequestUrl(String serviceId){
        int index=requestNum.addAndGet(1)-1;
        Map<String,List<String>> map= ServiceMap.getServiceMap();
        if(StringUtils.isEmpty(serviceId)){
            throw new RuntimeException("serviceId 不可为空");
        }
        List<String> serviceList=map.get("/"+serviceId);
        if(serviceList==null||serviceList.size()==0){
            throw new RuntimeException("找不到相关服务");
        }
        index=index&(serviceList.size()-1);
        return "http://"+serviceList.get(index);
    }

}