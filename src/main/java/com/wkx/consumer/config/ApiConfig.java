package com.wkx.consumer.config;

import com.wkx.consumer.annotation.Path;
import com.wkx.consumer.annotation.Provider;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.I0Itec.zkclient.ZkClient;
import org.reflections.Reflections;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ApiConfig implements ApplicationRunner{

    private static final OkHttpClient CLIENT = new OkHttpClient();

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
//        Reflections reflections=new Reflections("");
//        Set<Class<?>> annotated= reflections.getTypesAnnotatedWith(Provider.class);
//        Set<String> serviceList=annotated.stream().map(c-> "/"+c.getAnnotation(Provider.class).serviceName()).collect(Collectors.toSet());
//        String zkServers=AppConfigurer.getProperty("zookeeper.servers");
//        ZkClient zkClient=new ZkClient(zkServers);
//        Map<String,List<String>> serviceMap=ApiMap.getServiceMap();
//        for (String service:serviceList){
//            if(zkClient.exists(service)){
//                List<String> pathList=zkClient.getChildren(service);
//                serviceMap.put(service,pathList);
//                zkClient.subscribeChildChanges(service, (s, list) -> serviceMap.put(service,list));
//            }
//        }
//        annotated.forEach(c-> {
//            Object obj=Proxy.newProxyInstance(c.getClassLoader(), new Class[]{c}, (proxy, method, args) -> {
//                String serviceName=proxy.getClass().getAnnotation(Provider.class).serviceName();
//                System.out.println(serviceName);
//                Path path=method.getAnnotation(Path.class);
//                if(path!=null){
//                    List<String> ipList=ApiMap.getServiceMap().get(serviceName);
//                    if(ipList!=null&&ipList.size()>0){
//                        String url="http://"+ipList.get(0)+path.path();
//                        System.out.println(url);
//                        Request httpRequest = new Request.Builder().url(url).build();
//                        Response httpResponse=CLIENT.newCall(httpRequest).execute();
//                        ResponseBody responseBody = httpResponse.body();
//                        if(httpResponse.isSuccessful()&&responseBody!=null){
//                            String body = responseBody.string();
//                            System.out.println(body);
//                            return body;
//                        }
//                    }
//                }
//                return null;
//            });
//            String names[]=c.getName().split(".");
//            String beanName=names[names.length-1];
//            BeanFactory.registerBean(beanName,obj);
//        });
//        BeanFactory.registerBean("zkClient",zkClient);
//        System.out.println("111111");
//        System.out.println(BeanFactory.getBean("testApi"));
//        System.out.println("111111");
    }

}
