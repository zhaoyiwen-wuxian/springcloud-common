package com.trench.plugin;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.trench.annotation.IdAlias;
import com.trench.annotation.ToMore;
import com.trench.annotation.ToOne;
import com.trench.batis.page.ThreadObjectPage;
import com.trench.util.MybatisUtil;
import com.trench.util.PluginUtil;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Statement;
import java.util.*;

/**
 * 结果集自动映射插件
 */
@Intercepts(
        @Signature(
                type = ResultSetHandler.class,
                method = "handleResultSets",
                args = {Statement.class}
        )
)
public class ResuletAutoPlugin implements Interceptor {

    private Logger log = LoggerFactory.getLogger(ResuletAutoPlugin.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        //判断是否开启自动映射
        Boolean autoFlag = ThreadObjectPage.getAutoFlag();
        if (autoFlag == null || !autoFlag)
            //未开启自动映射，跳过当前插件
            return invocation.proceed();

        ResultSetHandler resultSetHandler = (ResultSetHandler) MybatisUtil.getNoProxyobject(invocation.getTarget());

        MetaObject statmentObject = SystemMetaObject.forObject(resultSetHandler);
        //获得当前核心的configuration对象
        Configuration configuration = (Configuration) statmentObject.getValue("configuration");
        //获得映射处理器
        MappedStatement mappedStatement = (MappedStatement) statmentObject.getValue("mappedStatement");

        //通过映射处理器获得当前返回的结果映射集
        List<ResultMap> resultMaps = mappedStatement.getResultMaps();
        if (!CollectionUtils.isEmpty(resultMaps)) {
            //结果集不为空
            for (ResultMap resultMap : resultMaps) {

                //如果字段映射ResultMapping对象集合不为空，说明已经存在自定义映射操作，则跳过自动映射操作
                if (!CollectionUtils.isEmpty(resultMap.getResultMappings())) {
                    continue;
                }

                //判断configuration中是否已经存在id相同的映射集
                if(!configuration.hasResultMap(resultMap.getId())) {
                    //自动化处理映射关系
                    resultMapHandler(configuration, resultMap);
                }
            }
        }

        return invocation.proceed();
    }

    /**
     * 处理ResultMap对象
     */
    private void resultMapHandler(Configuration configuration, ResultMap resultMap){
        //创建映射对象集合
        List<ResultMapping> resultMappings = new ArrayList<>();
        List<ResultMapping> idResultMappings = new ArrayList<>();
        List<ResultMapping> propertyResultMappings = new ArrayList<>();
        Set<String> mappedColumn = new HashSet<>();
        Set<String> mappedProperties = new HashSet<>();
        //不存在当前类型的映射集，
        //获得当前返回映射对象的Class对象
        Class<?> resultClass = resultMap.getType();
        //解析其中的所有属性值
        Field[] fields = resultClass.getDeclaredFields();

        //是否有复杂查询
        Boolean hasNestedResultMaps = false;
        //循环处理映射管理
        hasNestedResultMaps = getHasNestedResultMaps(configuration, fields, hasNestedResultMaps, mappedColumn, mappedProperties, idResultMappings, propertyResultMappings, resultMappings);
        PluginUtil.configurationSaveAddResultMap(configuration, resultMap, resultMappings, hasNestedResultMaps, idResultMappings, propertyResultMappings, mappedColumn, mappedProperties);
    }



    private Boolean getHasNestedResultMaps(Configuration configuration, Field[] fields, Boolean hasNestedResultMaps, Set<String> mappedColumn, Set<String> mappedProperties, List<ResultMapping> idResultMappings, List<ResultMapping> propertyResultMappings, List<ResultMapping> resultMappings) {
        
        for (Field field : fields) {
            //设置私有使用权限
            field.setAccessible(true);
            //不处理静态和最终的属性对象
            if (Modifier.isStatic(field.getModifiers())
                    || Modifier.isFinal(field.getModifiers())) continue;

            //数据库的列名
            String colume = null;
            //实体类的字段名
            String property = field.getName();
            //实体类的类型
            Class javaType = field.getType();
            //数据库的类型
            JdbcType jdbcType = null;
            //类型转换器
            TypeHandler typeHandler = null;
            //标识
            ResultFlag resultFlag = null;
            //是否映射的字段
            boolean exist = true;
            //子映射的id号
            String nestResultMapId = null;

            //判断对象的注解 --
            if (field.isAnnotationPresent(TableField.class)) {
                //存在当前注解
                TableField tableField = field.getAnnotation(TableField.class);

                //判断是否跳过
                exist = tableField.exist();
                //获得数据库中的列名称
                colume = tableField.value();
                //获得数据库类型
                jdbcType = tableField.jdbcType();
                //处理类型转换器
                if (tableField.typeHandler() != UnknownTypeHandler.class) {
                    try {
                        typeHandler = tableField.typeHandler().newInstance();
                    } catch (Exception e) {
                        log.error("[AUTO_MAPPING] - 反射创建类型转换器时异常 - {}" + tableField.typeHandler().getName());
                    }
                }
            }

            //判断对象的注解 -- 结合MyBatis-Plus的注解
            if (field.isAnnotationPresent(TableId.class)) {
                //存在当前注解，说明这是一个主键对象
                TableId tableId = field.getAnnotation(TableId.class);

                IdAlias idAlias = field.getAnnotation(IdAlias.class);

                //获得数据库中的列名称
                colume = Optional.ofNullable(idAlias)
                        .map(idAlias1 -> idAlias1.value())
                        .orElse(tableId.value());

                //设置主键标识
                resultFlag = ResultFlag.ID;
            }

            //如果数据库列名为空，实体类的字段名就对应列名
            if (StringUtils.isEmpty(colume)) {
                colume = property;
            }

            //判断是否存在对一的操作
            if (field.isAnnotationPresent(ToOne.class)){
                //存在对一
                ToOne toOne = field.getAnnotation(ToOne.class);
                //表示当前字段需要自动映射
                exist = true;
                //表示存在复杂查询
                hasNestedResultMaps = true;
                //对多的字段，没有对应的数据库字段名
                colume = null;
                //获得对多的子类型
                Class sType = javaType;
                //设置数据库类型
                jdbcType = null;
                //传子类型对应的ResultMap对象
                nestResultMapId = UUID.randomUUID().toString();
                ResultMap sResultMap = new ResultMap
                        .Builder(configuration, nestResultMapId, sType, new ArrayList<>(), false)
                        .build();
                //TODO 递归处理ResultMap
                resultMapHandler(configuration, sResultMap);
            }

            //判断是否存在对多的操作
            if (field.isAnnotationPresent(ToMore.class)) {
                //存在对多
                ToMore toMore = field.getAnnotation(ToMore.class);
                //表示当前字段需要自动映射
                exist = true;
                //表示存在复杂查询
                hasNestedResultMaps = true;
                //对多的字段，没有对应的数据库字段名
                colume = null;
                //获得对多的子类型
                Class sType = toMore.type();
                //设置数据库类型
                jdbcType = null;
                //传子类型对应的ResultMap对象
                nestResultMapId = UUID.randomUUID().toString();
                ResultMap sResultMap = new ResultMap
                        .Builder(configuration, nestResultMapId, sType, new ArrayList<>(), false)
                        .build();
                //TODO 递归处理ResultMap
                resultMapHandler(configuration, sResultMap);
            }
            Result result = new Result(colume, jdbcType, typeHandler, resultFlag, exist, nestResultMapId);

            //如果exist为false，则跳过该字段的自动化映射处理
            if (!result.exist) continue;

            //创建对应的ResultMapping映射对象
            ResultMapping resultMapping = new ResultMapping.Builder(configuration, property)
                    .column(result.colume)//数据库列名
                    .javaType(javaType)//java字段的类型
                    .jdbcType(result.jdbcType)//数据库字段类型
                    .flags(Collections.singletonList(result.resultFlag))//设置标识
                    .typeHandler(result.typeHandler)
                    .nestedResultMapId(result.nestResultMapId)//子映射关系的id号
                    .lazy(true)
                    .build();

            if (!StringUtils.isEmpty(result.colume)) {
                mappedColumn.add(result.colume.toUpperCase());
            }
            mappedProperties.add(property);

            //加入集合中
            if (result.resultFlag != null && result.resultFlag == ResultFlag.ID) {
                idResultMappings.add(resultMapping);
            }
            propertyResultMappings.add(resultMapping);
            resultMappings.add(resultMapping);
        }
        return hasNestedResultMaps;
    }

    private static class Result {
        public final String colume;
        public final JdbcType jdbcType;
        public final TypeHandler typeHandler;
        public final ResultFlag resultFlag;
        public final boolean exist;
        public final String nestResultMapId;

        public Result( String colume, JdbcType jdbcType, TypeHandler typeHandler, ResultFlag resultFlag, boolean exist, String nestResultMapId) {
            this.colume = colume;
            this.jdbcType = jdbcType;
            this.typeHandler = typeHandler;
            this.resultFlag = resultFlag;
            this.exist = exist;
            this.nestResultMapId = nestResultMapId;
        }
    }
}
