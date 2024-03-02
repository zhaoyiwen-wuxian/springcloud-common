package com.trench.util;

import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

public class PluginUtil {
    private static Logger log = LoggerFactory.getLogger(PluginUtil.class);
    public static void configurationSaveAddResultMap(Configuration configuration, ResultMap resultMap, List<ResultMapping> resultMappings, Boolean hasNestedResultMaps, List<ResultMapping> idResultMappings, List<ResultMapping> propertyResultMappings, Set<String> mappedColumn, Set<String> mappedProperties) {
        if (resultMappings.size() > 0) {
            try {
                //将映射结果集放入ResultMap对象中
                Class<? extends ResultMap> reClass = resultMap.getClass();
                Field resultMappingsField = reClass.getDeclaredField("resultMappings");
                //开启私有使用权限
                resultMappingsField.setAccessible(true);
                //使用反射将映射集合放入resultMapping中
                resultMappingsField.set(resultMap, resultMappings);

                //设置是否存在嵌套类型
                Field hasNestedResultMapsFeild = reClass.getDeclaredField("hasNestedResultMaps");
                hasNestedResultMapsFeild.setAccessible(true);
                hasNestedResultMapsFeild.set(resultMap, hasNestedResultMaps);

                //设置其他相关属性
                Field idResultMappingsFeild = reClass.getDeclaredField("idResultMappings");
                idResultMappingsFeild.setAccessible(true);
                idResultMappingsFeild.set(resultMap, idResultMappings);

                Field propertyResultMappingsFeild = reClass.getDeclaredField("propertyResultMappings");
                propertyResultMappingsFeild.setAccessible(true);
                propertyResultMappingsFeild.set(resultMap, propertyResultMappings);

                Field mappedColumnsFeild = reClass.getDeclaredField("mappedColumns");
                mappedColumnsFeild.setAccessible(true);
                mappedColumnsFeild.set(resultMap, mappedColumn);

                Field mappedPropertiesFeild = reClass.getDeclaredField("mappedProperties");
                mappedPropertiesFeild.setAccessible(true);
                mappedPropertiesFeild.set(resultMap, mappedProperties);

                //操作ResultMap完成，放入Configuration对象中
                configuration.addResultMap(resultMap);
            } catch (Exception e) {
                log.error("[AUTO_MAPPING] - 反射注入自动映射时异常");
            }
        }
    }

    public static void propertyParam(List<ParameterMapping> parameterMappings, BoundSql boundSql, Object parameterObject, TypeHandlerRegistry typeHandlerRegistry, Configuration configuration) {
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
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
                    log.debug("[SQL] params[" + i + "] - [" + propertyName + ":" + value + "]");
                }
            }
        }
    }
}
