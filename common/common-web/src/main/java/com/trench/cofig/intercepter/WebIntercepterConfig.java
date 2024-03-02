package com.trench.cofig.intercepter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.util.List;

//拦截器注册类
public class WebIntercepterConfig  implements WebMvcConfigurer {

    @Autowired
    private List<HandlerInterceptorAdapter> interceptorAdapters;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if(interceptorAdapters !=null){
            interceptorAdapters.stream().forEach(handlerInterceptorAdapter -> {
                registry.addInterceptor(handlerInterceptorAdapter);
            });
        }
    }
}
