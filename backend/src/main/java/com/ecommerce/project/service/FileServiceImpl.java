package com.ecommerce.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Value("${project.path.files.image}")
    private String imageUploadPath;

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        return uploadImage(imageUploadPath, file);
    }

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        if(originalFileName == null)
            throw new RuntimeException("File name is null");

        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if(dotIndex != -1){
            extension = originalFileName.substring(dotIndex);
        }

        String fileName = UUID.randomUUID() + extension;

        File folder = new File(path);
        if(!folder.exists()){
            boolean status = folder.mkdirs();
        }
        String filePath = Paths.get(path,fileName).toString();

        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

}
