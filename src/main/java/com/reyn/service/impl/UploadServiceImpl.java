package com.reyn.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.reyn.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class UploadServiceImpl implements UploadService {

    @Value("${system-diy-cfg.upload-dir}")
    private String uploadDir;

    @Autowired
    private OSS ossClient;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.domain:}")
    private String domain;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Override
    public String save2Local(MultipartFile file) {
        try {
            // 1. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // 2. 构建文件保存路径
            Path uploadPath = Paths.get(uploadDir).resolve(uniqueFilename);
            Files.createDirectories(uploadPath.getParent()); // 确保目录存在

            // 3. 保存文件到本地
            file.transferTo(uploadPath.toFile());

            // 4. 返回文件在本地磁盘的完整保存路径
            return uploadPath.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败", e);
        }
    }

    @Override
    public String save2OSS(MultipartFile file) {
        try {
            // 1. 生成唯一文件名和路径
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 使用日期作为目录结构，避免文件过多
            String datePath = DateUtil.format(DateUtil.date(), "yyyy/MM/dd");
            String uniqueFilename = datePath + "/" + IdUtil.fastSimpleUUID() + fileExtension;

            // 2. 获取文件输入流
            InputStream inputStream = file.getInputStream();

            // 3. 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFilename, inputStream);

            // 4. 上传文件到OSS
            ossClient.putObject(putObjectRequest);

            // 5. 关闭输入流
            inputStream.close();

            // 6. 构建文件访问URL
            String fileUrl;
            if (domain != null && !domain.isEmpty()) {
                // 如果配置了自定义域名
                fileUrl = domain + "/" + uniqueFilename;
            } else {
                // 使用默认OSS域名
                String cleanEndpoint = endpoint.replace("http://", "").replace("https://", "");
                fileUrl = "https://" + bucketName + "." + cleanEndpoint + "/" + uniqueFilename;
            }

            log.info("文件上传成功: {}", fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("OSS文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
}
