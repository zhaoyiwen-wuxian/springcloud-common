package com.trench.aspect;
import com.alibaba.fastjson.JSON;

import com.trench.annotation.RequiresPermissions;
import com.trench.entity.LoginUser;
import com.trench.util.RedisUtil;
import com.trench.util.common.JwtUtil;
import com.trench.util.common.TokenConstants;
import io.jsonwebtoken.Claims;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
@Component
@Aspect
public class AuthAspect {
    @Autowired
    private RedisUtil redisTemplate;

    @Around("@annotation(permissions)")
    public Object around(ProceedingJoinPoint joinPoint, RequiresPermissions permissions) throws Throwable {
        //得到自定义注解中的权限串
        String[] value = permissions.value();
        String permission = "";
        //取出第一个权限串拿来做比较
        if (value != null&&value.length>0){
            permission = value[0];
        }
        //得到登入用户
        LoginUser user= getLoginUser();
        if (Objects.isNull(user)) {
            throw new RuntimeException("用户失效！");
        }

        //通过用户得到权限串
        List<String> permissionsList = user.getPermissions();

        //对比注解中定义的权限串跟redis中存放的权限串是否一致,一致则表示拥有该权限
        boolean b = hasRights(permission,permissionsList);
        if (!b){
            throw new RuntimeException("无权限访问");
        }
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }
    //判断权限串是否相等
    private boolean hasRights(String permission, List<String> permissionsList) {
        return permissionsList.contains(permission);
    }

    private LoginUser getLoginUser() {
        //得到当前的请求对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //得到请求头
        String header = request.getHeader(TokenConstants.LOGIN_AUTH_HEADER);
        //判断得到的请求头是否瞒住条件
        if (header.startsWith(TokenConstants.LOGIN_AUTH_BEARER)){
            //将前缀替换为空,拿到jwt加密的请求头
            String token = header.replace(TokenConstants.LOGIN_AUTH_BEARER, "");
            //解析请求头
            Claims claims = JwtUtil.parseToken(token);
            //判断解析出来的内容是否为空
            if (claims!=null){
                //内容不为空则获取uuid
                String uuid = claims.get(TokenConstants.USER_KEY).toString();
                //构造出redis中的key
                String key= getkey(uuid);
                //通过key获取user对象
                String s = redisTemplate.getCacheObject(key);
                return JSON.parseObject(s,LoginUser.class);
            }
        }
        return null;
    }
    //拼接得到redis中存放loginUser的key
    private String getkey(String uuid) {
        return TokenConstants.LOGIN_USER_REDIS_KEY+uuid;
    }
}
