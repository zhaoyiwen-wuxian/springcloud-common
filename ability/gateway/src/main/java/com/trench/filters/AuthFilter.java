package com.trench.filters;

import com.trench.batis.enums.CodeEnum;
import com.trench.config.WhiteListProperties;
import com.trench.util.RedisUtil;
import com.trench.util.TokenUtils;
import com.trench.util.common.JwtUtil;
import com.trench.util.common.TokenConstants;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AuthFilter implements Ordered, GlobalFilter {
    @Autowired
    private WhiteListProperties whiteListProperties;
    @Autowired
    private RedisUtil redisTemplate;

    //ip黑名单
    public static final List<String> BACK_LIST=null;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("全局网关开始工作了！");
        long startTime = System.currentTimeMillis();
        //得到请求对象
        ServerHttpRequest request = exchange.getRequest();
        //得到请求路径
        String path = request.getURI().getPath();
        //判断ip是否为白名单
        boolean backIp = TokenUtils.getBackIp(request, BACK_LIST);
        if (!backIp){
            log.info("接口名称：{}  请求耗时：{}  ms",path, System.currentTimeMillis() - startTime);
            return TokenUtils.webFluxResponseWriter(exchange, CodeEnum.FORBIDDEN);
        }

        //判断是否在放行的白名单中,如果存在则放行
        List<String> whitelist = whiteListProperties.getWhitelist();
        //判断白名单的路径中是否包含当前请求的路径
        if(whitelist.contains(path)){
            log.info("接口名称：{}  请求耗时：{}  ms",path, System.currentTimeMillis() - startTime);
            return chain.filter(exchange);
        }
        //得到请求头
        String token = TokenUtils.getToken(request);
        //请求头为空则返回认真不成功
        if (StringUtils.isEmpty(token)) {
            log.info("接口名称：{}  请求耗时：{}  ms",path, System.currentTimeMillis() - startTime);
            return TokenUtils.webFluxResponseWriter(exchange, CodeEnum.UNAUTHORIZED);
        }
        //解析请求头
        Claims claims = JwtUtil.parseToken(token);
        //解析请求头为空则说明是错误请求头,则显示请求头无效
        if (Objects.isNull(claims)) {
            log.info("接口名称：{}  请求耗时：{}  ms",path, System.currentTimeMillis() - startTime);
            return TokenUtils.webFluxResponseWriter(exchange, CodeEnum.UNAUTHORIZED_TOKEN);
        }
        //得到经过jwt解密后的uuid值
        String uuid_token = claims.get(TokenConstants.USER_KEY).toString();
        //拼凑redis的key
        String key = TokenUtils.getKey(uuid_token);

        //根据key得到值(登录用户的字符串形式)
        //判断redis过期时间是否还有效
        long keyExpire = redisTemplate.getExpireTime(key);
        //现在的时间s
        long  start=System.currentTimeMillis()/1000;
        String loginUserInfo = redisTemplate.getCacheObject(key);
        if (keyExpire > 0&&(keyExpire-start)<=180) {
            if (!StringUtils.isEmpty(loginUserInfo)){
                redisTemplate.setCacheObject(key,loginUserInfo, 3, TimeUnit.HOURS);
            }
        }

        if(StringUtils.isEmpty(loginUserInfo)){
            log.info("接口名称：{}  请求耗时：{}  ms",path, System.currentTimeMillis() - startTime);
            return TokenUtils.webFluxResponseWriter(exchange,CodeEnum.UNAUTHORIZED_INVALID);
        }
        log.info("接口名称：{}  请求耗时：{}  ms",path, System.currentTimeMillis() - startTime);
        return chain.filter(exchange);     // 放行;
    }
    @Override
    public int getOrder() {
        return 0;
    }
}
