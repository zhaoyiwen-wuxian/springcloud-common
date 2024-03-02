package com.trench.batis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.trench.batis.factory.SnowFlakeFactory;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BatisEntity implements Serializable {
    @TableId(type = IdType.INPUT)
    private Long id;

    private Date createTime=new Date();

    private Date updateTime=new Date();

    private Integer status=0;

    private Integer del=0;
    /**我这里采用了自己写的雪花算法去生成ID*/
    public BatisEntity(){
        this.id= SnowFlakeFactory.getSnowFlake().nextId();
    }
}
