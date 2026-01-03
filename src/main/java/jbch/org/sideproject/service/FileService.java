package jbch.org.sideproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFileName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = uuid + extension;
        
        String storedFilePath = uploadDir + File.separator + fileName;

        File dest = new File(storedFilePath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        
        // 웹에서 접근 가능한 URL 경로를 반환
        return "/uploads/" + fileName;
    }

    public void deleteFile(String storedFilePath) throws IOException {
        if (storedFilePath != null) {
            // URL 경로를 실제 파일 시스템 경로로 변환하여 삭제
            String fileName = storedFilePath.substring("/uploads/".length());
            String absolutePath = uploadDir + File.separator + fileName;
            Files.deleteIfExists(Paths.get(absolutePath));
        }
    }
}
