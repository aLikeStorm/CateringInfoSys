package com.aks.cateringinfosys.config;

import com.aks.cateringinfosys.mappers.ImageMapper;
import com.aks.cateringinfosys.utils.SystemConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/25 19:40
 * @packagename com.aks.cateringinfosys.config
 * @classname TimerTask
 * @description
 */
@Component
public class TimerTask {
    @Autowired
    ImageMapper imageMapper;
    Logger logger = LoggerFactory.getLogger(TimerTask.class);
    /**
     * 每周三凌晨2点定时执行定时删除数据库中未存图片路径的图片
     */
    @Scheduled(cron = "0 0 2 ? * WED")
    void scheduledDeletion() {
        //打开指定的文件夹目录发现所有文件
        File file = new File(SystemConstants.IMAGE_UPLOAD_DIR);
        File[] files = file.listFiles();
        // 遍历文件
        for (File image : files) {
            String fileName = null;
            if (image.isFile()) {
                fileName = SystemConstants.IMAGEPATH+image.getName();
                // 数据库中没有则删除这个图片
                Integer flag = imageMapper.countImageByName(fileName);
                if (flag != 1) {
                    logger.warn("系统在"+ LocalDateTime.now().atZone(ZoneId.systemDefault())+"删除一张数据库已经不存在的图片"+fileName);
                     image.delete();
                }
            }
        }
    }
}
