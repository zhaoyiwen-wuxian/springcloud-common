package com.trench.exce;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.alibaba.fastjson.JSON;
import com.trench.batis.enums.CodeEnum;
import com.trench.batis.r.R;
import com.trench.batis.retrun.RUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * sentinel全局异常处理
 *
 * */
@Component
public class ExceptionHandlePage implements BlockExceptionHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        response.setContentType("application/json;charset=utf-8");
        R data=null;
        if (e instanceof FlowException) {
            data = RUtil.err(CodeEnum.RESTRICTED);
        }else if (e instanceof DegradeException){
            data = RUtil.err(CodeEnum.DOWNGRADED);
        }else if (e instanceof ParamFlowException){
            data = RUtil.err(CodeEnum.EXCEPTION);
        }else if (e instanceof AuthorityException){
            data = RUtil.err(CodeEnum.AUTHORIZATION);
        }else if (e instanceof SystemBlockException){
            data = RUtil.err(CodeEnum.SYSTEM_BLOCK_EXCEPTION);
        }
        response.getWriter().write(JSON.toJSONString(data));
    }
}