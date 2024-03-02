package com.trench.util;

import com.trench.batis.page.PageUtil;
import com.trench.plugin.PagePlugin;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class MybatisUtil {

    //非代理的内置对象
    public static Object getNoProxyobject(Object target){
        MetaObject metaObject = SystemMetaObject.forObject(target);
        while (metaObject.hasGetter("h")){
            //获取代理对象，持有的对象
            target = metaObject.getValue("h.target");
            metaObject=SystemMetaObject.forObject(target);
        }
        //返回非代理对象
        return target;
    }

    //计算sql的from的位置
    public static Integer getMasaterFromIndex(int begIndex,String sql){
        int count = 0;//括号的数量
        int fromIndex = sql.indexOf("from", begIndex);

        //搜索位置
        int selectIndex = fromIndex;
        int bIndex = -1;
        while((bIndex = sql.lastIndexOf("(", selectIndex)) != -1){
            count++;
            selectIndex = bIndex - 1;
        }

        //搜索反括号
        selectIndex = fromIndex;
        int eIndex = -1;
        while((eIndex = sql.lastIndexOf(")", selectIndex)) != -1){
            count--;
            selectIndex = eIndex - 1;
        }
        if (count==0){
            return fromIndex;
        }else {
            return getMasaterFromIndex(fromIndex + 1,sql);
        }
    }

    /**
     * 计算共有多少条记录
     * @return
     */
    public static Integer getTotal(Invocation invocation, MetaObject statmentObject, String sql, boolean isSunSql, int beginParams, int endParams){
        //获得参数管理器
        ParameterHandler ph = (ParameterHandler) statmentObject.getValue("delegate.parameterHandler");

        //拼接sql - 获得计算总数的sql语句
        //select count(1) from student where age = ? and sex = ?
        int formIndex = MybatisUtil.getMasaterFromIndex(0, sql);
        String countsql = "select count(1) as total " + sql.substring(formIndex);
        //去除order by - 提升查询条数的性能
        int orderbyIndex = -1;
        if((orderbyIndex = countsql.indexOf("order by")) != -1){
            countsql = countsql.substring(0,  orderbyIndex);
        }

        //获得计算总数的sql
        log.debug("[PAGING SQL] paging count sql - " + countsql);

        //执行该sql语句

        //获得Connection链接
        Connection conn = (Connection) invocation.getArgs()[0];
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(countsql);
            if (!isSunSql) {
                //通过参数处理器设置参数
                ph.setParameters(ps);
            } else if (beginParams != -1 && endParams != -1){
                //需要手动设置参数
                BoundSql boundSql = (BoundSql) statmentObject.getValue("delegate.boundSql");
                //获取全局配置对象
                Configuration configuration = (Configuration) statmentObject.getValue("delegate.configuration");
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                //获得参数列表
                List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
                Object parameterObject = boundSql.getParameterObject();

                for (int i = beginParams; i <= endParams; i++){
                    ParameterMapping parameterMapping = parameterMappings.get(i);
                    if (parameterMapping.getMode() != ParameterMode.OUT) {
                        Object value;
                        String propertyName = parameterMapping.getProperty();
                        if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
                            value = boundSql.getAdditionalParameter(propertyName);
                        } else if (parameterObject == null) {
                            value = null;
                        } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                            value = parameterObject;
                        } else {
                            MetaObject metaObject = configuration.newMetaObject(parameterObject);
                            value = metaObject.getValue(propertyName);
                        }
                        TypeHandler typeHandler = parameterMapping.getTypeHandler();
                        JdbcType jdbcType = parameterMapping.getJdbcType();
                        if (value == null && jdbcType == null) {
                            jdbcType = configuration.getJdbcTypeForNull();
                        }
                        try {
                            log.debug("[PAGING SQL] paging count sql params" + ((i - beginParams) + 1) + " - [" + value + "]");
                            typeHandler.setParameter(ps, (i - beginParams) + 1, value, jdbcType);
                        } catch (TypeException | SQLException e) {
                            throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                        }
                    }
                }
            }
            //执行sql语句
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }


    public static PreparedStatement getPreparedStatement(Invocation invocation, MetaObject statmentObject, PagePlugin.Result result, PageUtil page, String sql) throws InvocationTargetException, IllegalAccessException {
        int count = MybatisUtil.getTotal(invocation, statmentObject, result.totalSql, result.isSunSql, result.paramBegin, result.paramEnd);
        page.setPageCount(count);
        //设置总页码
        if (page.getPageSize() == null || page.getPageSize() <= 0) page.setPageSize(10);//如果没有每页显示的条数，默认设置为10条
        page.setPageTotal(page.getPageCount() % page.getPageSize() == 0 ?
                page.getPageCount() / page.getPageSize() :
                page.getPageCount() / page.getPageSize() + 1);

        //调整page的页码
        if (page.getPageNum() <= 0) {
            page.setPageNum(1);
        } else if (page.getPageTotal() > 0 && page.getPageNum() > page.getPageTotal()) {
            page.setPageNum(page.getPageTotal());
        }


        //开始分页
        //去除最后的分号
        if(sql.endsWith(";")){
            sql = sql.substring(0, sql.length() - 1);
        }

        //组装分页属性
        String limit = " limit " +  ((page.getPageNum() - 1) * page.getPageSize()) + "," + page.getPageSize();

        //如果存在子SQL，需要将sql设置回原sql
        if (result.isSunSql) {
            sql = sql.replaceAll("\\s*limit\\s+\\?", limit);
        } else {
            //拼接limit关键字
            sql += limit;
        }

        //回设最新的SQL语句
        statmentObject.setValue("delegate.boundSql.sql", sql);
        log.debug("[PAGING SQL] exec sql - {}", sql);

        //放行，进行sql编译
        PreparedStatement ps = (PreparedStatement) invocation.proceed();
        log.debug("[PAGING SQL] paging end...");
        return ps;
    }
}
