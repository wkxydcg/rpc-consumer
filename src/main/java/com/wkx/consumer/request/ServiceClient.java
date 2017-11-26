package com.wkx.consumer.request;

import com.alibaba.fastjson.JSONObject;
import com.wkx.consumer.annotation.BodyKey;
import com.wkx.consumer.annotation.GetPath;
import com.wkx.consumer.annotation.PostPath;
import com.wkx.consumer.config.BeanFactory;
import com.wkx.consumer.loadBalance.LoadBalance;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
public class ServiceClient {

    private static LoadBalance loadBalance= BeanFactory.getBean(LoadBalance.class);

    private static CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();

    static {
        httpClient.start();
    }

    public static Object execute(String serviceName, Method method,Object [] args){
        GetPath getPath=method.getAnnotation(GetPath.class);
        PostPath postPath=method.getAnnotation(PostPath.class);
        if(getPath!=null&&postPath!=null){
            throw new RuntimeException("注解使用错误");
        }
        Class returnType=method.getReturnType();
        if(getPath!=null){
            String url=loadBalance.getRequestUrl(serviceName)+getPath.path();
            HttpGet httpGet=new HttpGet(url);
            return executeGet(httpGet,returnType);
        }else{
            String url=loadBalance.getRequestUrl(serviceName)+postPath.path();
            HttpPost httpPost=new HttpPost(url);
            Annotation[][] annotations=method.getParameterAnnotations();
            int index=0;
            String key;
            Object value;
            JSONObject requestJson=new JSONObject();
            for (Annotation[] annotation1 : annotations) {
                for (Annotation annotation : annotation1) {
                    if (annotation.getClass() == BodyKey.class) {
                        key = ((BodyKey) annotation).value();
                        value = args[index];
                        requestJson.put(key, value);
                    }
                    index++;
                }
            }
            try {
                StringEntity entity=new StringEntity(requestJson.toJSONString());
                httpPost.setEntity(entity);
                return executePost(httpPost,returnType);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new RuntimeException("错误");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Object executeGet(HttpGet httpGet, Class returnType)  {
        Future<HttpResponse> future = httpClient.execute(httpGet,null);
        return handleResult(future,returnType);
    }

    @SuppressWarnings("unchecked")
    private static Object executePost(HttpPost httpPost, Class returnType)  {
        Future<HttpResponse> future = httpClient.execute(httpPost,null);
        return handleResult(future,returnType);
    }

    @SuppressWarnings("unchecked")
    private static Object handleResult(Future<HttpResponse> future,Class returnType){
        try {
            HttpResponse response=future.get();
            String responseStr= EntityUtils.toString(response.getEntity());
            JSONObject json=JSONObject.parseObject(responseStr);
            return JSONObject.toJavaObject(json,returnType);
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("错误");
    }
}
