package com.trench.cofig.hander;

import com.trench.cofig.util.ApplicationUtil;
import com.trench.cofig.valid.LinkageValid;
import com.trench.cofig.valid.KenValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

//自定义校验处理器
public class LinkageValidHander implements ConstraintValidator<LinkageValid,Object> {

   private LinkageValid custemValid;

    @Override
    public void initialize(LinkageValid constraintAnnotation) {
     this.custemValid=constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        //执行校验逻辑 true成功

        if (value!=null){

            Class<? extends KenValid> handerClass = custemValid.hander();
            KenValid kenValid = ApplicationUtil.getBean(handerClass);
              //通过去判断开发者提供的对象
            if (kenValid==null) return true;
           return  kenValid.isValid(custemValid,value);
        }
        return true;
    }
}
