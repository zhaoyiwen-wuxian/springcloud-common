package com.trench.input;

import com.trench.cofig.valid.LinkageValid;
import com.trench.valid.AgeAndBirthDayValid;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

@Data
@LinkageValid(message = "格式不正确", hander = AgeAndBirthDayValid.class)
public class StuderInput implements Serializable {

    @NotBlank(message = "不能为空")
    private String name;

    @Min(value = 0,message = "不能超过")
    @Max(value = 100, message = "不能超过")
    @NotNull
    private Integer age;

    @Email(message = "格式不正确")
    @NotBlank(message = "不能为空")
    private String emil;

    private String sex;

    @NotNull(message = "不能为空")
    @Past(message = "范围不正确")
    @DateTimeFormat(pattern = "YYY-MM-dd")
    private Date birthDay;
}
