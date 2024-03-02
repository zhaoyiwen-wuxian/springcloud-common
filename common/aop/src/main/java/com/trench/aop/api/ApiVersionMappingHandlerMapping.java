package com.trench.aop.api;

import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

//支持接口多版本的controller多版本的信息， 对reuestmapping的扩展，
public class ApiVersionHandler extends RequestMappingHandlerMapping {

    //初始化时候进行触发
    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        return super.getCustomTypeCondition(handlerType);
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        return super.getCustomMethodCondition(method);
    }
}
