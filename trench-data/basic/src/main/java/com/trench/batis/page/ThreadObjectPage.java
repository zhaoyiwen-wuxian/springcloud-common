package com.trench.batis.page;

//分页调用方法
public class ThreadObjectPage {
    private static ThreadLocal<PageUtil> pageUtilThreadLocal=new ThreadLocal<>();

    private static ThreadLocal<Boolean> autoThreadLocal = new ThreadLocal<>();


    public static void setPage(Integer pageNum,Integer pageSize, boolean isBatch){
        PageUtil pageUtil=new PageUtil();
        pageUtil.setPageSize(pageSize);
        pageUtil.setPageNum(pageNum);
        pageUtil.setEnable(!isBatch);
        pageUtilThreadLocal.set(pageUtil);
    }

    public static void setPage(Integer pageNum,Integer pageSize){
        PageUtil pageUtil=new PageUtil();
        pageUtil.setPageSize(pageSize);
        pageUtil.setPageNum(pageNum);
        pageUtilThreadLocal.set(pageUtil);
    }

    public static PageUtil getPage(){
        return pageUtilThreadLocal.get();
    }

    public static void clear(){
        pageUtilThreadLocal.remove();
    }

    /*
    结果集自动映射相关的方法
     */
    public static Boolean getAutoFlag(){
        return autoThreadLocal.get();
    }

    public static void setAutoFlag(Boolean autoFlag) {
        autoThreadLocal.set(autoFlag);
    }
}
