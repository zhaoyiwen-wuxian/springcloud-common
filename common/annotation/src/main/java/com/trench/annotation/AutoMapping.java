package com.trench.annotation;

import java.lang.annotation.*;

//自动映射字段的属于
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoMapping {
}
