package com.trench.batis.enums;

public enum CodeEnum {
    RESTRICTED(-1,"The interface is restricted"),
    DOWNGRADED(-2,"The interface has been downgraded"),
    EXCEPTION(-3,"Parameter current limit exception"),
    AUTHORIZATION(-4,"Authorization exception"),
    SYSTEM_BLOCK_EXCEPTION(-5,"The interface is SystemBlockException"),
    SULL(200,"成功"),
    UNAUTHORIZED(401,"认证不成功！"),
    UNAUTHORIZED_TOKEN(402,"Token信息丢失或无效！"),
    FORBIDDEN(403,"黑名单！"),
    FAIL(500,"失败"),
    PARAMENT_ERROR(501,"参数校验异常"),
    UNAUTHORIZED_INVALID(502,"登录用户无效！"),
    BUSY(504,"服务器忙，请稍后再试！")
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
