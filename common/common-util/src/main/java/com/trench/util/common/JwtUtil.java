package com.trench.util.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Map;

/**
 * 类名：
 * 功能：jwt工具类
 */
public class JwtUtil {
	private static String key = "hzit";
	//1. 生成jwttoken值
	public static String createToken(Map<String,Object> map){
		return Jwts.builder()
					.setId("1001")
					// .setExpiration(new Date(System.currentTimeMillis() + seconds))  // 设置一分钟到期
					.setSubject("jwtSubject")
					.setClaims(map)
					.signWith(SignatureAlgorithm.HS256,key).compact();
	}
	//2. 解析token
	public static Claims parseToken(String token){
		return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
	}
}
