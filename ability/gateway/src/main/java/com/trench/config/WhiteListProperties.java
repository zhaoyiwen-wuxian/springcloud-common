package com.hzit.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "security.ignore")
@Configuration
@Data
public class WhiteListProperties {
    private List<String> whitelist;
}
