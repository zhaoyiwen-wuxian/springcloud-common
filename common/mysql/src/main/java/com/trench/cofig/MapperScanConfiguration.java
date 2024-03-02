package com.trench.cofig;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"com.trench.dao"})
public class MapperScanConfiguration {
}
