# rpc-consumer
一个简单版本的rpc框架客户端 基于http 协议 使用zookeeper作为注册中心 

## 配置
application.properties配置
zookeeper.servers=${host1}:${port1},${host2}:${port2}

## 使用demo
API定义:
```
package com.wkx.rpc.consumerdemo.api;
import com.wkx.consumer.annotation.GetPath;
import com.wkx.consumer.annotation.ServiceId;

@ServiceId(serviceName="test")
public interface TestApi {

    @GetPath(path = "/hello")
    String getHello();
    
}
```

controller层调用：
```
package com.wkx.rpc.consumerdemo.controller;
import com.wkx.rpc.consumerdemo.api.TestApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/hello")
public class TestController {

    @Autowired
    private TestApi testApi;
    @RequestMapping("/test")
    public String helloWorld(){
        return testApi.getHello();
    }
    
}
```

### 注解说明
@ServiceId 远程服务明名</br>
@GetPath get方式请求远程服务 参数:path(url相对路径)</br>
@PostPath post方式请求远程服务 参数:path(url相对路径)</br>

### 负载说明
提供两种负载模式 轮循和随机
默认采用轮循
若想采用随机的方式
将RotationLoadBalance 注入spring容器即可






