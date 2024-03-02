package com.trench.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "security.ignore")  //表示在yml中添加security:ignore[0]: 接口名称-
@Configuration
@Data
public class WhiteListProperties {
    private List<String> whitelist;
}
