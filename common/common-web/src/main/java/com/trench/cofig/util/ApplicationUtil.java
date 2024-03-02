package com.trench.cofig.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

//spring 容器等工具类
public class ApplicationUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public  static  <T> T getBean(Class<T> tClass){
        return applicationContext.getBean(tClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationUtil.applicationContext=applicationContext;
    }
}
