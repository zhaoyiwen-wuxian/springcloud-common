package com.trench.cofig.valid;

import com.trench.cofig.hander.CustemValidHander;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**自定义校验注解
 *
 *
 * */
@Documented
@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
//校验注解必须标记的注解，方法validatedBy用户指定实际的校验过程的类
@Constraint(validatedBy = CustemValidHander.class)
public @interface CustemValid {
      String message() default "校验未通过";

//      校验分组信息
      Class<?> [] groups() default {};
    /**
     * 校验对负载
     * */
    Class<? extends Payload> [] payload() default {};

    //指定开发者自定义等校验处理器
    Class<? extends KenValid>  hander() ;
}
