package com.aks.cateringinfosys.controller;

import cn.hutool.core.util.StrUtil;
import com.aks.cateringinfosys.dto.Result;
import com.aks.cateringinfosys.utils.SystemConstants;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static com.aks.cateringinfosys.utils.SystemConstants.IMAGEPATH;

/**
 * @author 安克松
 * @version 1.0.0
 * @date 2023/6/13 14:46
 * @packagename com.aks.cateringinfosys.controller
 * @classname FileController
 * @description
 */
@RestController
@RequestMapping("/file")
public class FileController {
    @PostMapping("/uploadImages")
    public Result uploadImages(MultipartFile[] images) throws IOException {
        ArrayList<String> imageList = new ArrayList<>();
        for (MultipartFile image : images) {
            String oldFileName = image.getOriginalFilename();
            String suffix = StrUtil.subAfter(oldFileName, ".", true);
            String prefix = null;
            if ( "jpeg".equals(suffix) ||
                    "png".equals(suffix) ||
                    "jpg".equals(suffix)||
                    "gif".equals(suffix)) {
                prefix = UUID.randomUUID().toString();
            } else {
                return Result.fail("上传图片失败，请重试");
            }
            String newFileName = IMAGEPATH+prefix+"."+suffix;
            imageList.add(newFileName);
            image.transferTo(new File(SystemConstants.IMAGE_UPLOAD_DIR,newFileName));
        }
        return Result.ok(imageList);

    }
}
