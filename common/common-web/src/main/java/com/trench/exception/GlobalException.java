package com.trench.exception;

import com.trench.batis.r.R;
import com.trench.batis.retrun.RUtil;
import com.trench.batis.enums.CodeEnum;
import com.trench.cofig.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**异常处理*/
@RestControllerAdvice
@Slf4j
public class GlobalException {

    @ExceptionHandler(Throwable.class)
    public R exceptionHandel(Throwable throwable){
        //统一获取当前请求的url
        try {
            HttpServletRequest httpServletRequest = RequestUtil.getHttpServletRequest();
            if (httpServletRequest!=null){
                log.error("GlobalException-throwable:{},获取请求的url:{}",throwable,httpServletRequest.getRequestURI());
            }else {
                log.error("GlobalException-throwable:{}",throwable);
            }
        }catch (Exception e){
            log.error("GlobalException-throwable:{}",throwable);
        }

        return RUtil.err(CodeEnum.FAIL);
    }

    /**参数校验的的返回值和异常处理
     * from
     * */
    @ExceptionHandler(BindException.class)
    public R bindExceptionHandel(BindException exception){
        log.error("bindExceptionHandel-{}",exception);
        return RUtil.err(CodeEnum.PARAMENT_ERROR,exception.getBindingResult().getAllErrors()
                .stream().map(objectError -> objectError.getDefaultMessage())
                .collect(Collectors.toSet()));
    }

    /**参数校验的的返回值和异常处理
     * RequestBody
     * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R methodArgumentNotValidException(MethodArgumentNotValidException exception){
        log.error("MethodArgumentNotValidException-{}",exception);
        return RUtil.err(CodeEnum.PARAMENT_ERROR,exception.getBindingResult()
                .getAllErrors().stream().map(objectError ->
                        objectError.getDefaultMessage())
                .collect(Collectors.toSet()));
    }

    /**参数校验的的返回值和异常处理
     * 行参数列表进行的校验
     * */
    @ExceptionHandler(ConstraintViolationException.class)
    public R constraintViolationException(ConstraintViolationException exception){
        log.error("ConstraintViolationException-{}",exception);
        return RUtil.err(CodeEnum.PARAMENT_ERROR,exception.getConstraintViolations()
                .stream().map(objectError -> objectError.getMessage())
                .collect(Collectors.toSet()));
    }

}
