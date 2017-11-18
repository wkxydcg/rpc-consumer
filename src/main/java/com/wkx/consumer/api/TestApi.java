package com.wkx.consumer.api;

import com.wkx.consumer.annotation.Path;
import com.wkx.consumer.annotation.Provider;

@Provider(serviceName = "test")
public interface TestApi {

    @Path(path = "/hello")
    String hello();

}
