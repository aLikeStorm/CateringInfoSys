package com.aks.cateringinfosys.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/3/28 10:11
 * @packagename com.aks.utils
 * @classname RedisIdWorker
 * @description 基于redis生成全局唯一ID    64位
 */
@Data
@AllArgsConstructor
@Component
public class RedisIdWorker {
    private static final long BEGIN_TIMESTAMP = 1672531200L;
    private static final int COUNT_BITS = 32; //表示时间戳位于32位
    //获取一秒内自增长id
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //通过下面的语句可以生成一个开始时间戳
        //LocalDateTime of = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        //System.out.println(of.toEpochSecond(ZoneOffset.UTC));

    /**
     * keyPerfix作为不同业务的区分
     * @param
     * @return
     */
    public long nextId() {
        //生成时间戳
        LocalDateTime nowTime = LocalDateTime.now();
        long nowSecond = nowTime.toEpochSecond(ZoneOffset.UTC);
        long timeStamp = nowSecond - BEGIN_TIMESTAMP;
        return timeStamp;
    }
}
