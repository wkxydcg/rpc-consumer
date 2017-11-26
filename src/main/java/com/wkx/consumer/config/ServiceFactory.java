package com.wkx.consumer.config;

import com.wkx.consumer.annotation.ServiceId;
import com.wkx.consumer.request.ServiceClient;
import org.I0Itec.zkclient.ZkClient;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ServiceFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
        Reflections reflections=new Reflections("");
        Set<Class<?>> annotated= reflections.getTypesAnnotatedWith(ServiceId.class);
        Set<String> serviceList=annotated.stream().map(c-> "/"+c.getAnnotation(ServiceId.class).serviceName()).collect(Collectors.toSet());
        String zkServers= ServiceEnv.getProperty("zookeeper.servers");
        ZkClient zkClient=new ZkClient(zkServers);
        Map<String,List<String>> serviceMap= ServiceMap.getServiceMap();
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
            ServiceFactory.registerBean(beanName,obj);
        });
        ServiceFactory.registerBean("zkClient",zkClient);
    }

    @SuppressWarnings("unchecked")
    private <T> T getProxy(Class<T> c){
        return (T) Proxy.newProxyInstance(c.getClassLoader(), new Class[]{c}, (proxy, method, args) -> {
            Class interfaces[]=proxy.getClass().getInterfaces();
            Class subInterface=interfaces[0];
            if("toString".equals(method.getName())){
                return getBeanName(subInterface);
            }
            String serviceName=((ServiceId)subInterface.getAnnotation(ServiceId.class)).serviceName();
            return ServiceClient.execute(serviceName,method,args);
        });
    }

    private static String getBeanName(Class c){
        String names[]=c.getName().split("\\.");
        String className=names[names.length-1];
        return Character.toLowerCase(className.charAt(0))+className.substring(1);
    }

    private static void registerBean(String name, Object obj) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        beanFactory.registerSingleton(name,obj);
    }
}
