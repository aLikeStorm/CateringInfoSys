package com.aks.cateringinfosys.config;

import com.aks.cateringinfosys.entry.Restaurant;
import com.aks.cateringinfosys.mappers.CommentMapper;
import com.aks.cateringinfosys.mappers.ImageMapper;
import com.aks.cateringinfosys.mappers.RestaurantMapper;
import com.aks.cateringinfosys.utils.SystemConstants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

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
    @Autowired
    RestaurantMapper restaurantMapper;
    @Autowired
    CommentMapper commentMapper;
    Logger logger = LoggerFactory.getLogger(TimerTask.class);
    /**
     * 每周三凌晨2点定时执行定时删除数据库中未存图片路径的图片
     */
    @Scheduled(cron = "0 0 2 ? * WED")
    void scheduledDeleImage() {
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

    /**
     * 定时计算每个店铺的评分，每周
     */
    @Scheduled(cron = "0 0 2 ? * TUE,THU,SUN")
    void scheduledComputeRestScore() {
        // 统计有多少个店铺
        Integer count = restaurantMapper.countData();
        Integer pageSum = count / 100 + 1;
        for (int i = 0; i < pageSum; i++) {
            PageHelper.startPage(i+1,100);
            List<Restaurant> restaurants = restaurantMapper.queryRestByName(null, null, null);
            PageInfo<Restaurant> restaurantPageInfo = new PageInfo<>(restaurants);
            List<Restaurant> list = restaurantPageInfo.getList();
            list.stream().forEach(restaurant -> {
                Long restId = restaurant.getRestId();
                Float score = getRestaurantScore(restId);
                restaurantMapper.updateScore(restId,score);
            });
        }
    }

    private Float getRestaurantScore(Long restId){
        Integer countComment = commentMapper.countComment(restId);
        if (countComment == null || countComment == 0) {
            return new Float(1);
        }
        // todo 此处为五分制评分需要转化为最大为1的四位小数
        Float avg_score = commentMapper.queryAvgScore(restId);
        BigDecimal score = new BigDecimal(avg_score);
        BigDecimal radix = new BigDecimal(5);
        // todo 四舍五入计算分数
        BigDecimal outcome = score.divide(radix, 4, RoundingMode.HALF_UP);
        return outcome.floatValue();
    }

}
