package com.trench.aop.api;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "api.version.name")  //表示在yml中添加:api:version:name = 版本号的-名称列如：api-version  如果不填那么默认为api-version
@Configuration
@Data
public class ApiVersionProperties {
    private String version;
}
