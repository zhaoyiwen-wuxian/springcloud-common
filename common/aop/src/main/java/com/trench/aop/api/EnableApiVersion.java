package com.trench.aop.api;

//多版本接口多启动注解

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ApiVersionMappingHandlerMapping.class)
public @interface EnableApiVersion {
}
