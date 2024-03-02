package com.trench.batis.r;
import com.trench.batis.page.PageBaseRetrun;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class R<T> extends PageBaseRetrun {

    private Integer code;

    private String message;

    private T data;
}
