package com.wkx.consumer.api;

import com.wkx.consumer.annotation.BodyKey;
import com.wkx.consumer.annotation.Path;
import com.wkx.consumer.annotation.PostPath;
import com.wkx.consumer.annotation.ServiceId;
import lombok.Builder;
import org.springframework.web.bind.annotation.RequestParam;

@ServiceId(serviceName = "test")
public interface TestApi {

    @Path(path = "/hello")
    String hello();

    @PostPath(path = "/hello")
    String hello1(@RequestParam("1") String a, @BodyKey("2") String b,@BodyKey("3") String c,@RequestParam("4")String d);

}
