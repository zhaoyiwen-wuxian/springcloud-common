package com.trench.plugin;

import com.trench.batis.page.PageUtil;
import com.trench.batis.page.ThreadObjectPage;
import com.trench.util.MybatisUtil;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;

//自定义分页插件
@Intercepts({@Signature(
        type = StatementHandler.class,//指定争强的内置对象
        method = "prepare", //指定内置对象中的方法
        args = {Connection.class, Integer.class}//指定内置对象中的参数
)
})
public class PagePlugin implements Interceptor {
    private Logger log = LoggerFactory.getLogger(PagePlugin.class);
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //获取非代理的目标对象
        StatementHandler statementHandler = (StatementHandler) MybatisUtil.getNoProxyobject(invocation.getTarget());
//        System.out.println("Mybatis插件获取的当前对象：" + statementHandler);
        //获得当前执行的SQL语句
        MetaObject statmentObject = SystemMetaObject.forObject(statementHandler);
        //获得需要编译的sql语句
        BoundSql boundSql = (BoundSql) statmentObject.getValue("delegate.boundSql");
        //获得sql
        String sql = boundSql.getSql().toLowerCase().trim().replace("\n", "").replaceAll("\\s+", " ");

        String totalSql = sql;
        //判断该sql是否为查询语句
        if(!sql.startsWith("select")){
            //不是查询语句，无需分页
            return invocation.proceed();
        }

        //获取分页的Page对象并且是开启了分页的，否则无法分页
        PageUtil page = ThreadObjectPage.getPage();
        if (page == null || !page.isEnable()) {
            //找不到分页对象，无法分页
            return invocation.proceed();
        }
        log.debug("[PAGING SQL] paging begin...");
        log.debug("[PAGING SQL] paging sql - [" + sql + "]");

        //判断该sql语句是否包含limit
        int selectIndex = 0;
        int limitIndex = -1;
        int paramBegin = -1;
        int paramEnd = -1;
        boolean isSunSql = false;
        if((limitIndex = sql.indexOf("limit")) != -1){
            isSunSql = true;
            //包含指定分页形式，截取需要分页的主sql
            selectIndex = sql.lastIndexOf("select", limitIndex);
            //截取主sql语句
            totalSql = sql.substring(selectIndex, limitIndex);
            log.debug("[PAGING SQL] has substring page sql - [{}]", totalSql);

            //判断totalSql中是否存在参数?
            int bIndex = 0;//记录开始的位置
            int pIndex = -1;//参数的下标
            int pCount = 0;//参数的数量
            while ((pIndex = totalSql.indexOf("?", bIndex)) != -1) {
                pCount++;
                bIndex = pIndex + 1;
            }

            if (pCount > 0) {
                //如果存在参数，则要判断这些参数在原SQL语句中的位置
                bIndex = selectIndex;
                int bpCount = 0;
                while ((pIndex = sql.lastIndexOf("?", bIndex)) != -1) {
                    bpCount++;
                    bIndex = pIndex - 1;
                }

                paramBegin = bpCount;
                paramEnd = bpCount + pCount - 1;
                log.debug("[PAGING SQL] substring page sql param beging - [{}]", paramBegin);
                log.debug("[PAGING SQL] substring page sql param end - [{}]", paramEnd);
            }
        }
        Result result = new Result(totalSql, paramBegin, paramEnd, isSunSql);


        //调用封装的方法，获取当前查询的总条数
        //返回statement对象交给MyBatis进行后续的sql执行操作
        return MybatisUtil.getPreparedStatement(invocation, statmentObject, result, page, sql);
    }



    public static class Result {
        public final String totalSql;
        public final int paramBegin;
        public final int paramEnd;
        public final boolean isSunSql;

        public Result(String totalSql, int paramBegin, int paramEnd, boolean isSunSql) {
            this.totalSql = totalSql;
            this.paramBegin = paramBegin;
            this.paramEnd = paramEnd;
            this.isSunSql = isSunSql;
        }
    }
}
