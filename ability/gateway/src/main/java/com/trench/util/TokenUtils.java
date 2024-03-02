package com.trench.util;

import com.alibaba.fastjson.JSON;
import com.trench.batis.enums.CodeEnum;
import com.trench.batis.retrun.RUtil;
import com.trench.util.common.TokenConstants;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class TokenUtils {

    // redis的key
    public static String getKey(String uuid_token) {
        return TokenConstants.LOGIN_USER_REDIS_KEY + uuid_token;
    }

    //定义将指定的信息输出到页面中
    public static Mono<Void> webFluxResponseWriter(ServerWebExchange exchange, CodeEnum codeEnum) {
        ServerHttpResponse response = exchange.getResponse();
        DataBuffer buffer = response.bufferFactory().wrap(JSON.toJSONString(RUtil.err(codeEnum)).getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    //在当前请求中获取请求头
    public static String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(TokenConstants.LOGIN_AUTH_HEADER);
        //判断请求头是否为空
        if (!StringUtils.isEmpty(token)) {
            //判断请求头是否以bearer开头
            if (token.startsWith(TokenConstants.LOGIN_AUTH_BEARER)) {
                //替换bearer为空
                return token.replaceAll(TokenConstants.LOGIN_AUTH_BEARER, "");
            }
        }
        return null;
    }


    public static boolean getBackIp(ServerHttpRequest request, List<String> BACK_LIST){
        String ip= request.getHeaders().getHost().getHostString();
        //查询数据库，看这个ip是否存在在黑名单里面
        if (BACK_LIST.contains(ip)){
            return true;
        }else {
            return false;
        }
    }
}
