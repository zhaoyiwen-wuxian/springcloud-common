package com.trench.feign;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "feign")
@Data
public class FeignParamterProperties {
    //参数列表
    private List<String> paramNames;
}
