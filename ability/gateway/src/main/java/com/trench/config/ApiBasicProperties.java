package com.trench.config;

import com.trench.basic.ApiBasic;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "api.name")//表示在yml中添加api:name: apiBasics[0]:apiName=studer  pattern=/studer/**
@Configuration
@Data
public class ApiBasicProperties {
    private List<ApiBasic> apiBasics;
}
