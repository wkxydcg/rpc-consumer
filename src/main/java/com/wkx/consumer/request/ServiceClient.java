package com.wkx.consumer.request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wkx.consumer.annotation.Path;
import com.wkx.consumer.util.BeanFactory;
import com.wkx.consumer.loadBalance.LoadBalance;
import com.wkx.consumer.util.ClassUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ServiceClient {

    private static LoadBalance loadBalance= BeanFactory.getBean(LoadBalance.class);

    private static CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();

    static {
        httpClient.start();
    }

    public static Object execute(String serviceName, Method method,Object [] args){
        Path requestPath=method.getAnnotation(Path.class);
        Class returnType=method.getReturnType();
        if(args==null||args.length==0){
            String url=loadBalance.getRequestUrl(serviceName)+requestPath.url();
            HttpGet httpGet=new HttpGet(url);
            return executeGet(httpGet,returnType);
        }else{
            String url=loadBalance.getRequestUrl(serviceName)+requestPath.url();
            HttpPost httpPost=new HttpPost(url);
            JSONArray requestJson=new JSONArray();
            for (Object obj:args){
                if(ClassUtils.checkIsBaseClass(obj.getClass())){
                    JSONObject json=new JSONObject();
                    json.put(obj.getClass().getName(),obj);
                    requestJson.add(json);
                }else{
                    requestJson.add(JSONObject.parseObject(JSONObject.toJSONString(obj)));
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
        httpPost.addHeader(new BasicHeader("Content-Type","application/json"));
        Future<HttpResponse> future = httpClient.execute(httpPost,null);
        return handleResult(future,returnType);
    }

    @SuppressWarnings("unchecked")
    private static Object handleResult(Future<HttpResponse> future,Class returnType){
        try {
            HttpResponse response=future.get();
            String responseStr= EntityUtils.toString(response.getEntity());
            if(ClassUtils.checkIsBaseClass(returnType)){
                return returnType.cast(responseStr);
            }
            JSONObject json=JSONObject.parseObject(responseStr);
            return JSONObject.toJavaObject(json,returnType);
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("错误");
    }
}
