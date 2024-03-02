package com.trench.plugin;

import com.trench.util.MybatisUtil;
import com.trench.util.PluginUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import java.math.BigDecimal;
import java.sql.Statement;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import java.math.RoundingMode;
import java.util.List;


//sql监控的记录插件
@Intercepts({@Signature(
        type = StatementHandler.class,//指定争强的内置对象
        method = "query", //指定内置对象中的方法
        args = {Statement.class, ResultHandler.class}//指定内置对象中的参数
),
        @Signature(
                type = StatementHandler.class,//指定争强的内置对象
                method = "update", //指定内置对象中的方法
                args = {Statement.class}//指定内置对象中的参数
        ),
        @Signature(
                type = StatementHandler.class,//指定争强的内置对象
                method = "queryCursor", //指定内置对象中的方法
                args = {Statement.class}//指定内置对象中的参数
        )
})
@Slf4j
public class SQLPlugin implements Interceptor {

    //核心拦截方法
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //sql语句和耗时
        StatementHandler statementHandler = (StatementHandler) MybatisUtil.getNoProxyobject(invocation.getTarget());//对象
        MetaObject statmentObject = SystemMetaObject.forObject(statementHandler);
        //获取全局配置对象
        Configuration configuration = (Configuration) statmentObject.getValue("delegate.configuration");
        //获得需要编译的sql语句
        BoundSql boundSql = (BoundSql) statmentObject.getValue("delegate.boundSql");
        //获得sql
        String sql = boundSql.getSql().toLowerCase().trim().replace("\n", "");

        //记录当前的sql语句
        log.debug("[SQL] executor - [" + sql + "]");


        //获取所有的参数
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Object parameterObject = boundSql.getParameterObject();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        PluginUtil.propertyParam(parameterMappings, boundSql, parameterObject, typeHandlerRegistry, configuration);

        //执行目标方法
        //记录执行前的时间
        long beginTime = System.currentTimeMillis();
        Object result = invocation.proceed();
        long end = System.currentTimeMillis();
        //计算耗时
        double tTime = new BigDecimal(end - beginTime).divide(new BigDecimal(1000)).setScale(6, RoundingMode.DOWN).doubleValue();
        log.debug("[SQL] take up time - [" + tTime + "s]");

        return result;
    }


}
