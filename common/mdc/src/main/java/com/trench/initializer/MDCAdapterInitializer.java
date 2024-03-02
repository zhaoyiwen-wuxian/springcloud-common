package com.trench.initializer;

import org.slf4j.MDC;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

public class MDCAdapterInitializer implements GenericApplicationListener {

    private static final String APPLICATION_CONFIG_PROPERTIES="configurationProperties";

    private static final String APPLICATION_NAME="configurationProperties";

    private static final String NEW_FRAME="newFrame";

    private static final String LOG_PATH="logPath";
    @Override
    public boolean supportsEventType(ResolvableType eventType) {
        return ApplicationEnvironmentPreparedEvent.class == eventType.getRawClass();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        ApplicationEnvironmentPreparedEvent environmentPreparedEvent=(ApplicationEnvironmentPreparedEvent)event;
       //当前环境
        ConfigurableEnvironment environment = environmentPreparedEvent.getEnvironment();
        //获取数据源
        MutablePropertySources propertySources = environment.getPropertySources();
        //获取固定参数
        PropertySource<?> propertySource = propertySources.get(APPLICATION_CONFIG_PROPERTIES);
        //获取参数
        String property = (String) propertySource.getProperty(APPLICATION_NAME);
        //TtlMDCAdapter.put();
        MDC.put(NEW_FRAME,property);
        MDC.put(LOG_PATH,property);

    }

    /**设置监听的优先级*/
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE+19;
    }
}
