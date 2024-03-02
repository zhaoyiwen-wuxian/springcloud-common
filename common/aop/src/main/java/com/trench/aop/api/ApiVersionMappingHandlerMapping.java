package com.trench.aop.api;

import com.trench.annotation.api.ApiVersion;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

//支持接口多版本的controller多版本的信息， 对reuestmapping的扩展，
public class ApiVersionMappingHandlerMapping extends RequestMappingHandlerMapping {

    //初始化时候进行触发
    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        ApiVersion apiVersion = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);
        return new ApiVersionCondition(apiVersion !=null ? apiVersion.value() :1.0);
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        ApiVersion apiVersion = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        if (apiVersion==null){
            apiVersion=AnnotationUtils.findAnnotation(method.getDeclaringClass(),ApiVersion.class);
        }
        return new ApiVersionCondition(apiVersion !=null ? apiVersion.value() :1.0);
    }
}
