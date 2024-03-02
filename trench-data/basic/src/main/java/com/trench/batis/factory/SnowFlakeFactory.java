package com.trench.batis.factory;

import com.trench.batis.loader.SnowFlakeLoader;
import com.trench.batis.snow.SnowFlake;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SnowFlakeFactory {
    /**
     * 默认的雪花算法句柄
     */
    private static final String DEFAULT_SNOW_FLAKE = "snow_flake";
    /**
     * 缓存SnowFlake对象
     */
    private static ConcurrentMap<String, SnowFlake> snowFlakeCache = new
            ConcurrentHashMap<>(2);
    public static SnowFlake getSnowFlake(long datacenterId, long machineId) {
        return new SnowFlake(datacenterId, machineId);
    }
    public static SnowFlake getSnowFlake() {
        return new SnowFlake(SnowFlakeLoader.getDataCenterId(), SnowFlakeLoader.getMachineId());
    }
    public static SnowFlake getSnowFlakeFromCache() {
        SnowFlake snowFlake = snowFlakeCache.get(DEFAULT_SNOW_FLAKE);
        if(snowFlake == null) {
            snowFlake = new SnowFlake(SnowFlakeLoader.getDataCenterId(),
                    SnowFlakeLoader.getMachineId());
            snowFlakeCache.put(DEFAULT_SNOW_FLAKE, snowFlake);
        }
        return snowFlake;
    }
    /**
     * 根据数据中心id和机器id从缓存中获取全局id
     * @param dataCenterId: 取值为1~31
     * @param machineId: 取值为1~31
     */
    public static SnowFlake getSnowFlakeByDataCenterIdAndMachineIdFromCache(Long dataCenterId, Long machineId) {
        if (dataCenterId > SnowFlake.getMaxDataCeneterNum() || dataCenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > SnowFlake.getMaxMachineNum() || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        String key =
                DEFAULT_SNOW_FLAKE.concat("_").concat(String.valueOf(dataCenterId)).concat("_").
                        concat(String.valueOf(machineId));
        SnowFlake snowFlake = snowFlakeCache.get(key);
        if(snowFlake == null) {
            snowFlake = new SnowFlake(dataCenterId, machineId);
            snowFlakeCache.put(key, snowFlake);
        }
        return snowFlake;
    }
}