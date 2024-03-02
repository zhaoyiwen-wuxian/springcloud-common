package com.trench.entity;

import lombok.Getter;

@Getter
public enum ResponseEnum {
    REQUEST_BODY_EMPTY("Y9971194BL000001", "请求体不能为空"),
    TIME_OUT_ERROR("Y9971194BL000002", "响应超时了"),

    RESPONSE_BODY_EMPTY("X9971194BL000001", "响应体不能为空"),
    RESPONSE_BODY_JSON_ERROR("X9971194BL000002", "响应消息不为json"),
    INNER_ERROR("0000000000000000", "系统内部异常"),
    NET_CONNECT_ERROR("Y9971194BL000003", "网络连接异常");


    private final String respCode;
    private final String respDesc;

    ResponseEnum(String respCode, String respDesc) {
        this.respCode = respCode;
        this.respDesc = respDesc;
    }

    @Override
    public String toString() {
        return "ResponseEnum{" +
                "respCode='" + respCode + '\'' +
                ", respDesc='" + respDesc + '\'' +
                '}';
    }
}