package com.trench.config;
import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.trench.batis.enums.CodeEnum;
import com.trench.batis.retrun.RUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**自定义限流设置修改默认的*/
@Configuration
public class GatewayConfiguration {

    @Resource
    private ApiBasicProperties apiBasicProperties;

    @PostConstruct
    public void init(){
        initCustomsApis();
        initGatewayRules();
        initBlackHandlers();

    }

    //自定义限流异常处理
    private void initBlackHandlers(){
        BlockRequestHandler blockExceptionHandler=new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                //可以通过throwable来判断各种异常
                return ServerResponse.status(HttpStatus.OK).
                        contentType(MediaType.APPLICATION_JSON).
                        body(BodyInserters.fromValue(RUtil.err(CodeEnum.BUSY)));
            }
        };
        GatewayCallbackManager.setBlockHandler(blockExceptionHandler);
    }

    //加载网关规则
    private void initGatewayRules(){
        Set<GatewayFlowRule> rules=new HashSet<>();
        apiBasicProperties.getApiBasics().stream().forEach(
                apiBasic -> {
                    rules.add(new GatewayFlowRule(apiBasic.getApiName())
                            .setCount(2)
                            .setIntervalSec(1)
                    );
                });
        GatewayRuleManager.loadRules(rules);
    }
    //初始化自定义api
    private void initCustomsApis(){
        Set<ApiDefinition> definitions=new HashSet<>();
        apiBasicProperties.getApiBasics().stream().forEach(apiBasic -> {
            definitions.add(new ApiDefinition(apiBasic.getApiName())
                    .setPredicateItems(new HashSet<ApiPredicateItem>(){
                        {
                            add(new ApiPathPredicateItem().setPattern(apiBasic.getPattern())
                                    .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX)
                            );
                        }}));
        });
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }
}
