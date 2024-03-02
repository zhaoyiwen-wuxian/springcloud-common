package com.trench.batis.enums;

public enum CustemEnum {
    MALE(1,"男"),
    GIRLS(2,"女")
    ;
    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    CustemEnum(Integer code, String message){
        this.code=code;
        this.message=message;
    }
}
