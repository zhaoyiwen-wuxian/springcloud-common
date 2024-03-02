package com.hzit.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import com.hzit.common.constants.TokenConstants;
import com.hzit.common.util.JwtUtil;
import com.hzit.common.util.R;
import com.hzit.gateway.config.WhiteListProperties;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class AuthFilter implements Ordered, GlobalFilter {
    @Autowired
    private WhiteListProperties whiteListProperties;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("全局网关开始工作了！");
        //得到请求对象
        ServerHttpRequest request = exchange.getRequest();
        //得到请求路径
        String path = request.getURI().getPath();
        //判断是否在放行的白名单中,如果存在则放行
        List<String> whitelist = whiteListProperties.getWhitelist();
        //判断白名单的路径中是否包含当前请求的路径
        if(whitelist.contains(path)){
            return chain.filter(exchange);
        }
        //得到请求头
        String token = getToken(request);
        //请求头为空则返回认真不成功
        if (StrUtil.isBlank(token)) {
            return webFluxResponseWriter(exchange, "认证不成功！", HttpStatus.UNAUTHORIZED.value());
        }
        //解析请求头
        Claims claims = JwtUtil.parseToken(token);
        //解析请求头为空则说明是错误请求头,则显示请求头无效
        if (Objects.isNull(claims)) {
            return webFluxResponseWriter(exchange, "Token信息丢失或无效！", HttpStatus.UNAUTHORIZED.value());
        }
        //得到经过jwt解密后的uuid值
        String uuid_token = claims.get(TokenConstants.USER_KEY).toString();
        //拼凑redis的key
        String key = getKey(uuid_token);
        //根据key得到值(登录用户的字符串形式)
        String loginUserInfo = redisTemplate.opsForValue().get(key);
        if(StrUtil.isBlank(loginUserInfo)){
            return webFluxResponseWriter(exchange,"登录用户无效！", HttpStatus.UNAUTHORIZED.value());
        }

        return chain.filter(exchange);     // 放行;
    }

    // redis的key
    private String getKey(String uuid_token) {
        return TokenConstants.LOGIN_USER_REDIS_KEY + uuid_token;
    }
    //定义将指定的信息输出到页面中
    private Mono<Void> webFluxResponseWriter(ServerWebExchange exchange, String msg, int code) {
        ServerHttpResponse response = exchange.getResponse();
        R fail = R.fail(code, msg);
        DataBuffer buffer = response.bufferFactory().wrap(JSON.toJSONString(fail).getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    //在当前请求中获取请求头
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(TokenConstants.LOGIN_AUTH_HEADER);
        //判断请求头是否为空
        if (StrUtil.isNotBlank(token)) {
            //判断请求头是否以bearer开头
            if (token.startsWith(TokenConstants.LOGIN_AUTH_BEARER)) {
                //替换bearer为空
                return token.replaceAll(TokenConstants.LOGIN_AUTH_BEARER, "");
            }
        }
        return null;
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
