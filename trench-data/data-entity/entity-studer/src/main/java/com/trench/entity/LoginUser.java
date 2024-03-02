package com.trench.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser {
	/**
	 * 用户唯一标识
	 */
	private String token;

	/**
	 * 权限列表
	 */
	private List<String> permissions;

	/**
	 * 用户信息
	 */
	private User user;
}
