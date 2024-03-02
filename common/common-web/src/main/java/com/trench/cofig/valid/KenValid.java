package com.trench.cofig.valid;

//当前数据校验的实现接口
public interface KenValid<T> {

     boolean isValid(LinkageValid custemValid, T value);


}
