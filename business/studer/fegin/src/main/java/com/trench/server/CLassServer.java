package com.trench.server;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "business-class") //服务名称
public interface CLassServer {

    @RequestMapping("/class/{tid}")
    String queryTeaName(@PathVariable Integer id);
}
