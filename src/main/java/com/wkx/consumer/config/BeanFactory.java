package com.wkx.consumer.config;

import com.wkx.consumer.annotation.Path;
import com.wkx.consumer.annotation.Provider;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
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
public class BeanFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    private static final OkHttpClient CLIENT = new OkHttpClient();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
        Reflections reflections=new Reflections("");
        Set<Class<?>> annotated= reflections.getTypesAnnotatedWith(Provider.class);
        Set<String> serviceList=annotated.stream().map(c-> "/"+c.getAnnotation(Provider.class).serviceName()).collect(Collectors.toSet());
        String zkServers=AppConfigurer.getProperty("zookeeper.servers");
        ZkClient zkClient=new ZkClient(zkServers);
        Map<String,List<String>> serviceMap=ApiMap.getServiceMap();
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
            BeanFactory.registerBean(beanName,obj);
        });
        BeanFactory.registerBean("zkClient",zkClient);
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static <T> T getBean(Class<T> c) {
        return context.getBean(c);
    }

    public static void registerBean(String name, Object obj) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        beanFactory.registerSingleton(name,obj);
    }

    @SuppressWarnings("unchecked")
    private <T> T getProxy(Class<T> c){
        return (T)Proxy.newProxyInstance(c.getClassLoader(), new Class[]{c}, (proxy, method, args) -> {
            Class interfaces[]=proxy.getClass().getInterfaces();
            Class subInterface=interfaces[0];
            if("toString".equals(method.getName())){
                return getBeanName(subInterface);
            }
            Path path=method.getAnnotation(Path.class);
            String serviceName=((Provider)subInterface.getAnnotation(Provider.class)).serviceName();
            String servicePath="/"+serviceName;
            if(path!=null){
                List<String> ipList=ApiMap.getServiceMap().get(servicePath);
                if(ipList!=null&&ipList.size()>0){
                    String url="http://"+ipList.get(0)+path.path();
                    Request httpRequest = new Request.Builder().url(url).build();
                    Response httpResponse=CLIENT.newCall(httpRequest).execute();
                    ResponseBody responseBody = httpResponse.body();
                    if(httpResponse.isSuccessful()&&responseBody!=null){
                        return responseBody.string();
                    }
                }
            }
            return null;
        });
    }

    private static String getBeanName(Class c){
        String names[]=c.getName().split("\\.");
        String className=names[names.length-1];
        return Character.toLowerCase(className.charAt(0))+className.substring(1);
    }
}
