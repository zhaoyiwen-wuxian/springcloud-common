package com.trench.page;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageUtil implements Serializable {

    private Integer pageNum;

    private Integer pageSize;

    private Integer pageCount;

    private Integer pageTotal;


}
