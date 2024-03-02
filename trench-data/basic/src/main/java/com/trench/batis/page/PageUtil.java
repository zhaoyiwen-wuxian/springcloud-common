package com.trench.batis.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class PageUtil<T> implements Serializable {

    /**
     * 分页默认关闭
     */
    @JsonIgnore
    private boolean enable = false;

    private Integer pageNum;

    private Integer pageSize;

    private Integer pageCount;

    private Integer pageTotal;


}
