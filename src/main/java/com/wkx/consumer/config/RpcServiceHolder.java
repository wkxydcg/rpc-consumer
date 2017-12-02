package com.wkx.consumer.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcServiceHolder {

    private static Map<String,List<String>> serviceMap=new ConcurrentHashMap<>();

    public static Map<String, List<String>> getServiceMap() {
        return serviceMap;
    }
}
