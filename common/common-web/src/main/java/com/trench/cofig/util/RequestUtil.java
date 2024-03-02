package com.trench.cofig.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

//全局获取接口
public class RequestUtil {

    //获取对象的请求方法
    public static HttpServletRequest getHttpServletRequest(){
        ServletRequestAttributes requestAttributes =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();

    }
}
