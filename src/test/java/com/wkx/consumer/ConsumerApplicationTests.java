package com.wkx.consumer;

import com.alibaba.fastjson.JSONObject;
import com.wkx.consumer.api.TestApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reflections.Reflections;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.reflect.Reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class ConsumerApplicationTests {

	@Test
	public void contextLoads() {
		Class c=TestApi.class;
		Arrays.stream(c.getMethods()).forEach(method -> {
			Annotation[][] annotations=method.getParameterAnnotations();


			System.out.println(JSONObject.toJSONString(annotations));
		});
	}

}
