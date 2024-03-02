package com.trench.cofig.enums;

public enum CodeEnum {
    SULL(200,"成功"),
    FAIL(500,"失败"),
    PARAMENT_ERROR(501,"参数校验异常")
    ;
    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    CodeEnum(Integer code, String message){
        this.code=code;
        this.message=message;
    }

}
