package com.trench.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * sql的插件，用于提示到idea中
 * */
@Data
@ConfigurationProperties(prefix = "com.mybatis.plugin")
public class PluginConfigInfo {

    private Sql sql;
    private Page page;

    private WebPageConfiguration webconfig;

    private AutoMappingConfiguration auto;
    @Data
    private static class Sql{
        private boolean enable;
    }
    @Data
    private static class Page{

        private boolean enable;
        private WebPageParamConfiguration key;

    }

    @Data
    public static class WebPageConfiguration {
        /**
         * 是否开启Web层兼容分页插件
         */
        boolean enable;
    }


    @Data
    public static class WebPageParamConfiguration {
        /**
         * 当前页的名称
         */
        String num;
        /**
         * 每页多少条
         */
        String size;
        /**
         * 总页码
         */
        String total;
        /**
         * 总条数
         */
        String count;
    }

    @Data
    public static class AutoMappingConfiguration {
        /**
         * 是否开启自动化映射
         */
        boolean enable;


    }
}
