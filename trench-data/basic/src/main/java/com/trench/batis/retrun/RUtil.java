package com.trench.cofig.util;

import com.trench.cofig.enums.CodeEnum;
import com.trench.util.r.R;

//生成快捷生成的r的方法
public class RUtil {

    public static <T> R create(T data){
        return new R(CodeEnum.SULL.getCode(), CodeEnum.SULL.getMessage(), data);
    }

    public static <T> R create(CodeEnum codeEnum,T data){
        return new R(codeEnum.getCode(),codeEnum.getMessage(),data);
    }

    public static  R create(CodeEnum codeEnum){
        return new R(codeEnum.getCode(),codeEnum.getMessage(),null);
    }

    public static <T> R err(){
        return new R<>(CodeEnum.FAIL.getCode(), CodeEnum.FAIL.getMessage(), null);
    }
    public static <T> R err(CodeEnum codeEnum){
        return new R<>(codeEnum.getCode(),codeEnum.getMessage(), null);
    }

    public static <T> R err(CodeEnum codeEnum,T data){
        return new R<>(codeEnum.getCode(),codeEnum.getMessage(), data);
    }

}
