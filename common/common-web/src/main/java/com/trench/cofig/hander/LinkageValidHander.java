package com.trench.cofig.hander;

import com.trench.cofig.valid.CustemValid;
import com.trench.cofig.valid.KenValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

//自定义校验处理器
public class CustemValidHander implements ConstraintValidator<CustemValid,Object> {

   private CustemValid  custemValid;

    @Override
    public void initialize(CustemValid constraintAnnotation) {
     this.custemValid=constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        //执行校验逻辑 true成功

        if (value!=null){

            Class<? extends KenValid> handerClass = custemValid.hander();

            KenValid kenValid=   //通过去判断开发者提供的对象
           return  kenValid.isValid();
        }
        return true;
    }
}
