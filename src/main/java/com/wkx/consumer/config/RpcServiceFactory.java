package com.wkx.consumer.config;

import com.wkx.consumer.annotation.RpcConsumer;
import com.wkx.consumer.env.ServiceEnv;
import com.wkx.consumer.request.ServiceClient;
import org.I0Itec.zkclient.ZkClient;
import org.reflections.Reflections;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RpcServiceFactory implements ApplicationContextInitializer{

    private static ConfigurableApplicationContext context;

    public void initialize(ConfigurableApplicationContext applicationContext){
        context = applicationContext;
        Reflections reflections=new Reflections("");
        Set<Class<?>> annotated= reflections.getTypesAnnotatedWith(RpcConsumer.class);
        Set<String> serviceList=annotated.stream().map(c-> "/"+c.getAnnotation(RpcConsumer.class).serviceName()).collect(Collectors.toSet());
        String zkServers= ServiceEnv.getProperty("zookeeper.servers");
        if(StringUtils.isEmpty(zkServers)) return;
        ZkClient zkClient=new ZkClient(zkServers);
        Map<String,List<String>> serviceMap= RpcServiceHolder.getServiceMap();
        for (String service:serviceList){
            if(zkClient.exists(service)){
                List<String> pathList=zkClient.getChildren(service);
                serviceMap.put(service,pathList);
                zkClient.subscribeChildChanges(service, (s, list) -> serviceMap.put(service,list));
            }
        }
        annotated.forEach(c-> {
            Object obj= getProxy(c);
            String beanName=getBeanName(c);
            RpcServiceFactory.registerBean(beanName,obj);
        });
        registerBean("zkClient",zkClient);
    }

    @SuppressWarnings("unchecked")
    private <T> T getProxy(Class<T> c){
        return (T) Proxy.newProxyInstance(c.getClassLoader(), new Class[]{c}, (proxy, method, args) -> {
            Class interfaces[]=proxy.getClass().getInterfaces();
            Class subInterface=interfaces[0];
            if("toString".equals(method.getName())){
                return getBeanName(subInterface);
            }
            String serviceName=((RpcConsumer)subInterface.getAnnotation(RpcConsumer.class)).serviceName();
            return ServiceClient.execute(serviceName,method,args);
        });
    }

    private static String getBeanName(Class c){
        String names[]=c.getName().split("\\.");
        String className=names[names.length-1];
        return Character.toLowerCase(className.charAt(0))+className.substring(1);
    }

    private static void registerBean(String name, Object obj) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        beanFactory.registerSingleton(name,obj);
    }

}
