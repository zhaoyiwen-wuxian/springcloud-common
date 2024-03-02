package com.trench.batis.constant;

/**
 * filter执行顺序：所有重试的filter都是-1之后，所以不重试的filter小于-1，重试的filter大于-1
 */
public class FilterOrderConstant {
    /**
     * 请求报文读取
     */
    public static final int BODY_FILTER_ORDER = -5000;
    /**
     * 读取缓存报文体
     */
    public static final int CACHE_BODY_FILTER_ORDER = -6000;
    /**
     * 响应报文读取
     */
    public static final int RESPONSE_FILTER_ORDER = -100;
    /**
     * 超时filter(应该在最后)
     */
    public static final int TIME_OUT_FILTER_ORDER = 5000;
    /**
     * 服务调度
     */
    public static final int SERVICE_FILTER_ORDER = -50;
    /**
     * 请求重试
     */
    public static final int REQ_RETRY_FILTER_ORDER = 4500;
}
