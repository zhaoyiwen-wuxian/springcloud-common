package com.trench.aop;

import com.trench.batis.page.ThreadObjectPage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 自动映射结果集的AOP拦截
 */
@Aspect
public class AutoMappingAop {

    /**
     * 被@AutoMapping注解标记的方法，查询数据库的结果会自动映射结果集
     * @return
     */
    @Around("@annotation(com.trench.annotation.AutoMapping)")
    public Object pageHandler(ProceedingJoinPoint joinPoint) throws Throwable {

        //开启自动映射
        ThreadObjectPage.setAutoFlag(true);

        //放行请求
        Object result = null;
        try {
            result = joinPoint.proceed();

            //关闭自动映射
            ThreadObjectPage.setAutoFlag(null);
        } catch (Throwable throwable) {
            //异常不处理，继续上抛
            throw throwable;
        }

        return result;
    }
}
