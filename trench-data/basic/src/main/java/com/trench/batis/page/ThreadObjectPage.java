package com.trench.page;

//分页调用方法
public class ThreadObjectPage {
    private static ThreadLocal<PageUtil> pageUtilThreadLocal=new ThreadLocal<>();

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
}
