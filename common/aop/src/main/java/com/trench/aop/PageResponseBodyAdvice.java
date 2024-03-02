package com.trench.webintercepter;

import com.trench.batis.page.PageUtil;
import com.trench.batis.page.ThreadObjectPage;
import com.trench.batis.r.R;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

//响应体处理器，返回信息 分页返回值

@RestControllerAdvice
public class PageResponseBodyAdvice  implements ResponseBodyAdvice<R> {

    //判断情况下触发判断
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.getMethod().getReturnType() == R.class;
    }

    @Override
    public R beforeBodyWrite(R body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        PageUtil page = ThreadObjectPage.getPage();
        if (page ==null) return body;
        //将分页信息设置并且进行返回给body
        body.setPage(page);
        return body;
    }
}
