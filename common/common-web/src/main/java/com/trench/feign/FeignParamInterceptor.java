package com.trench.feign;

import com.trench.cofig.util.RequestUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class FeignParamInterceptor implements RequestInterceptor {

    //请求参数数据
    @Autowired
    private FeignParamterProperties  properties;
    @Override
    public void apply(RequestTemplate requestTemplate) {
        List<String> paramNames = properties.getParamNames();
        if (CollectionUtils.isEmpty(paramNames)) return;
        //先获取参数，
        HttpServletRequest httpServletRequest = RequestUtil.getHttpServletRequest();
        paramNames.stream().forEach(param->{
            String parameter = httpServletRequest.getParameter(param);
            if (!StringUtils.isEmpty(parameter)){
                //放入请求头中
                requestTemplate.header(param,parameter);
            }

            //获取参数在请求头中
            String header = httpServletRequest.getHeader(param);
            if (!StringUtils.isEmpty(header)){
                requestTemplate.query(param,header);
            }
        });
    }
}
