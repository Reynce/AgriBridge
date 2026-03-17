package com.reyn.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    String save2Local(MultipartFile file);

    String save2OSS(MultipartFile file);
}
