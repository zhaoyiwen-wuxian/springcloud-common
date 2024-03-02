package com.trench.entity;

import com.trench.batis.entity.BatisEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class Studer extends BatisEntity {
    private String name;
    private Integer age;
    private String sex;
    private Date birthDay;
   /*@TableId(type = IdType.AUTO) 主键自增 数据库中需要设置主键自增
    @TableId(type = IdType.NONE) 默认 跟随全局策略走
    @TableId(type = IdType.UUID) UUID类型主键
    @TableId(type = IdType.ID_WORKER) 数值类型  数据库中也必须是数值类型 否则会报错
    @TableId(type = IdType.ID_WORKER_STR) 字符串类型   数据库也要保证一样字符类型
    @TableId(type = IdType.INPUT) 用户自定义了  数据类型和数据库保持一致就行*/
}
