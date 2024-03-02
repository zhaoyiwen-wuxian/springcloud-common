package com.trench.controller;

import com.trench.input.StuderInput;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/studer")
@Slf4j
@Validated
@RefreshScope //更新
public class StuderController {

    @PostMapping("/list")
    public String list(@Valid @RequestBody  StuderInput studerInput){
        log.info("接口请求成功");
        return "";
    }

    @PostMapping("/list1")
    public String list1(@NotBlank(message = "不能为空")
                        @Length(message = "长度超过范围",max = 6,min = 1) String pa){
        log.info("接口请求成功");
        return "";
    }
}
