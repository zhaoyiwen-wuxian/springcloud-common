package com.trench.annotation.api;

import java.lang.annotation.*;

//标注接口方法的版本信息，没有标注接口默认1.0版本
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {
    //版本信息
    double value();
}
