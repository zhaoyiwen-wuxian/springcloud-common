package com.trench.util;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trench.dao.StuderDao;
import com.trench.entity.Studer;
import com.trench.server.CLassServer;
import com.trench.server.StuderServer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class StuderServerImpl extends ServiceImpl<StuderDao, Studer>  implements StuderServer {

    @Resource
    private CLassServer cLassServer;
}
