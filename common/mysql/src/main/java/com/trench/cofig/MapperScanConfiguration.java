package com.trench.cofig;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.trench.aop.AutoMappingAop;
import com.trench.aop.PageAop;
import com.trench.plugin.PagePlugin;
import com.trench.plugin.ResuletAutoPlugin;
import com.trench.plugin.SQLPlugin;
import com.trench.property.PluginConfigInfo;
import com.trench.aop.webintercepter.PageIntercepter;
import com.trench.aop.PageResponseBodyAdvice;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan(basePackages = {"com.trench.dao"})
@EnableTransactionManagement
@EnableConfigurationProperties(value = PluginConfigInfo.class)
public class MapperScanConfiguration {

    @Bean
    @ConditionalOnProperty(name = "com.mybatis.plugin.sql.enable",havingValue = "true",matchIfMissing = false)
    public SQLPlugin getSQLPlugin(){
        return new SQLPlugin();
    }

    @Bean
    public OptimisticLockerInterceptor getOptimisticLockerInterceptor(){
        return new OptimisticLockerInterceptor();
    }

    //分页注册
    @Bean
    @ConditionalOnProperty(name = "com.mybatis.plugin.page.enable",havingValue = "true",matchIfMissing = false)
    public PagePlugin getPagePlugin(){return new PagePlugin();}


    //返回参数的设置
    @Bean
    @ConditionalOnProperty(name = "com.mybatis.plugin.page.enable",havingValue = "true",matchIfMissing = false)
    private PageResponseBodyAdvice getPageResponseBodyAdvice(){return new PageResponseBodyAdvice();}

    /**
     * 装配Mybatis 结果集自动映射插件
     * @return
     */
    @Bean
    //有Mybatis 和 MybatisPlus的环境才能使用
    @ConditionalOnBean({SqlSessionFactory.class, MybatisPlusAutoConfiguration.class})
    //指定属性值为true时才能加载,缺省时不加载
    @ConditionalOnProperty(prefix = "com.mybatis.plugin.auto", value = "enable", havingValue = "true", matchIfMissing = false)
    public ResuletAutoPlugin getAutoPlugin(){
        return new ResuletAutoPlugin();
    }

    /**
     * 自动映射的AOP
     */
    @Bean
    //有插件时才会加载
    @ConditionalOnBean(ResuletAutoPlugin.class)
    public AutoMappingAop getAutoMappingAop(){
        return new AutoMappingAop();
    }

    /**
     * 装配aop
     */
    @Bean
    //存在aop环境
    // 指定属性值为true时才能加载
    @ConditionalOnProperty(prefix = "com.mybatis.plugin.page", value = "enable", havingValue = "true", matchIfMissing = true)
    public PageAop getPageAop(){
        return new PageAop();
    }

    /**
     * 装配aop
     */
    @Bean
    //存在aop环境
    //当存在分页插件时，才会加载该AOP
    @ConditionalOnBean(PagePlugin.class)
    //web环境才能加载
    @ConditionalOnWebApplication
    //指定属性值为true时才能加载，缺省不加载
    @ConditionalOnProperty(prefix = "com.mybatis.plugin.webconfig", value = "enable", havingValue = "true", matchIfMissing = false)
    public PageIntercepter getWebPageAop(){
        return new PageIntercepter();
    }

}
