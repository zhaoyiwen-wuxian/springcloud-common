package com.trench.aop.webintercepter;

import com.trench.batis.page.BaseResult;
import com.trench.batis.page.PageUtil;
import com.trench.batis.page.ThreadObjectPage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

//分页拦截器
@Aspect
public class PageIntercepter {
    @Value("${com.mybatis.plugin.page.key.num:pageNum}")
    private String pNum;
    @Value("${com.mybatis.plugin.page.key.size:pageSize}")
    private String pSize;
    @Value("${com.mybatis.plugin.page.key.total:total}")
    private String pTotal;
    @Value("${com.mybatis.plugin.page.key.count:count}")
    private String pCount;
    @Around("@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller)")
    public Object pageHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        //获得请求对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //获得请求参数
        String pageNum = request.getParameter(pNum);
        String pageSize = request.getParameter(pSize);

        //封装Page对象
        PageUtil page = null;
        if (pageNum != null && pageSize != null) {
            //缓存到ThreadLocal中
            ThreadObjectPage.setPage(Integer.valueOf(pageNum), Integer.valueOf(pageSize));
        }

        //放行请求
        Object result = null;
        try {
            result = joinPoint.proceed();

            //获取请求线程中Page对象
            page = ThreadObjectPage.getPage();
            if (page != null) {
                //如果有分页信息，则修改返回对象
                if (result instanceof BaseResult) {
                    //将page分页设置到返回对象上
                    Map<String, Integer> pageMap = new HashMap<>();
                    pageMap.put(pNum, page.getPageNum());
                    pageMap.put(pSize, page.getPageSize());
                    pageMap.put(pTotal, page.getPageTotal());
                    pageMap.put(pCount, page.getPageCount());
                    ((BaseResult)result).setPage(pageMap);
                }
                //业务执行 清空分页缓存
                ThreadObjectPage.clear();
            }
        } catch (Throwable throwable) {
            //异常不处理，继续上抛
            throw throwable;
        }

        return result;
    }
}
