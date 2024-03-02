package com.trench.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 授权的自定义注解
 */
@Retention(RetentionPolicy.RUNTIME)//设置运行时有效
//ElementType.METHOD: 表示注解可以应用于方法上。
//ElementType.TYPE: 表示注解可以应用于类、接口、枚举、注解类型上。
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface RequiresPermissions {
    /**
     * 需要校验的权限码
     */
    String[]value() default {};
}

