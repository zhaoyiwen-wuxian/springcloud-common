package com.trench.aop.api;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {

    @Getter
    private  double apiVersion=1.0;

    public ApiVersionCondition(double apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Resource
    private ApiVersionProperties apiVersionProperties;
    private static String VERSION_NAME="api-version";
    @Override
    public ApiVersionCondition combine(ApiVersionCondition other) {
        return other;
    }

    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
        double apiversionParameter=1.0;
        String version = apiVersionProperties.getVersion();
        if (StringUtils.isEmpty(version)){
            version=VERSION_NAME;
        }
        String parameter = request.getHeader(version);
        if (StringUtils.isEmpty(parameter)){
            parameter=request.getParameter(version);
        }

        if (StringUtils.isEmpty(parameter)){
            try {
                apiversionParameter=Math.max(Double.valueOf(parameter),1.0);
            }catch (Exception e){
                apiversionParameter=1.0;
            }

        }

        if (this.getApiVersion()==apiversionParameter) return  this;

       return this;
    }

    @Override
    public int compareTo(ApiVersionCondition other, HttpServletRequest request) {
        return Double.compare(other.getApiVersion(),this.getApiVersion());
    }
}
