package com.trench.webintercepter;

import com.trench.batis.page.BaseResult;
import com.trench.batis.page.PageUtil;
import com.trench.batis.page.ThreadObjectPage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//分页拦截器
@Component
public class PageIntercepter extends HandlerInterceptorAdapter {
    @Value("${kenplugin.page.key.num:pageNum}")
    private String pNum;
    @Value("${kenplugin.page.key.size:pageSize}")
    private String pSize;
    @Value("${kenplugin.page.key.total:total}")
    private String pTotal;
    @Value("${kenplugin.page.key.count:count}")
    private String pCount;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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
                    pageMap.put(pTotal, page.getTotal());
                    pageMap.put(pCount, page.getCount());
                    ((BaseResult)result).setPage(pageMap);
                }
                //业务执行 清空分页缓存
            }
        } catch (Throwable throwable) {
            //异常不处理，继续上抛
            throw throwable;
        }

        return result;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadObjectPage.clear();
    }
}
