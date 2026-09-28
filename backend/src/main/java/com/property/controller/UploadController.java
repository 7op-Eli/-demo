package com.property.controller;

import com.property.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Tag(name = "文件上传")
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${file.upload.path:/data/property/uploads/}")
    private String uploadPath;

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10 MB

    @Operation(summary = "上传图片（支持多文件）")
    @PostMapping("/image")
    public Result<List<String>> uploadImage(@RequestParam("files") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            if (file.getSize() > MAX_SIZE) {
                return Result.error(400, "文件过大: " + file.getOriginalFilename() + " (max 10MB)");
            }
            String ext = getExtension(file.getOriginalFilename());
            if (ext == null || !ALLOWED_EXT.contains(ext.toLowerCase())) {
                return Result.error(400, "不支持的文件类型: " + ext + " (允许: jpg/png/gif/webp)");
            }
            try {
                String url = saveFile(file, ext);
                urls.add(url);
            } catch (IOException e) {
                log.error("文件保存失败", e);
                return Result.error(500, "文件保存失败: " + file.getOriginalFilename());
            }
        }
        return Result.success(urls);
    }

    @Operation(summary = "上传单张图片")
    @PostMapping("/image/single")
    public Result<String> uploadSingleImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return Result.error(400, "文件为空");
        if (file.getSize() > MAX_SIZE) return Result.error(400, "文件过大 (max 10MB)");
        String ext = getExtension(file.getOriginalFilename());
        if (ext == null || !ALLOWED_EXT.contains(ext.toLowerCase())) {
            return Result.error(400, "不支持的文件类型: " + ext);
        }
        try {
            return Result.success(saveFile(file, ext));
        } catch (IOException e) {
            log.error("文件保存失败", e);
            return Result.error(500, "文件保存失败");
        }
    }

    // ===== 内部 =====

    private String saveFile(MultipartFile file, String ext) throws IOException {
        // 按日期分目录: uploads/2026/06/20/uuid.ext
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path dir = Paths.get(uploadPath, dateDir);
        Files.createDirectories(dir);

        String filename = UUID.randomUUID().toString() + "." + ext.toLowerCase();
        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());

        // 返回相对路径（前端拼接 base URL 访问）
        return "/uploads/" + dateDir + "/" + filename;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return null;
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
