package com.trench.cofig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 核心的core的基础配置类
 * 全局的注解扫描
 * */
@Configuration
@ComponentScan(basePackages = {"com.trench"})
public class BaseConfiguration {
}
