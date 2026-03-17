package com.reyn.controller;

import cn.dev33.satoken.util.SaResult;
import com.reyn.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    /**
     * 单文件上传到本地
     */
    @PostMapping
    public SaResult uploadToLocal(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return SaResult.error("文件不能为空");
        }

        try {
            String filePath = uploadService.save2OSS(file);
            return SaResult.data(filePath).setMsg("文件上传成功");
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return SaResult.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 单文件上传到OSS
     */
    @PostMapping("/oss")
    public SaResult uploadToOSS(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return SaResult.error("文件不能为空");
        }

        try {
            String fileUrl = uploadService.save2OSS(file);
            return SaResult.data(fileUrl).setMsg("文件上传成功");
        } catch (Exception e) {
            log.error("OSS文件上传失败", e);
            return SaResult.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 多文件上传到OSS
     */
    @PostMapping("/oss/batch")
    public SaResult batchUploadToOSS(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return SaResult.error("文件列表不能为空");
        }

        List<String> fileUrls = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                if (!file.isEmpty()) {
                    String fileUrl = uploadService.save2OSS(file);
                    fileUrls.add(fileUrl);
                }
            } catch (Exception e) {
                log.error("批量上传文件失败: {}", file.getOriginalFilename(), e);
                failedFiles.add(file.getOriginalFilename() + ": " + e.getMessage());
            }
        }

        return SaResult.data(fileUrls)
                .setMsg("上传完成，成功: " + fileUrls.size() + "个，失败: " + failedFiles.size() + "个")
                .setData(failedFiles);
    }
}
