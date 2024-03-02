package com.trench.cofig.inter;

//当前数据校验的实现接口
public interface KenValid<T> {

     boolean isValid(CustemValid custemValid, T value);


}
