package com.hzit.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wzy
 * @since 2024-01-25
 */
@Getter
@Setter
@TableName("sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private String id;

    /**
     * 账号
     */
    private String usercode;

    /**
     * 姓名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
