package com.example.project.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.example.project.dto.FileDO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

public class FileUtil {
    // 파일 업로드 디렉토리 (웹 애플리케이션 루트 기준)
    private static final String UPLOAD_DIR = "Uploads";

    public static String getUploadPath(HttpServletRequest req) {
        String uploadPath = req.getServletContext().getRealPath("/uploads");
        if (uploadPath == null) {
            uploadPath = "C:" + File.separator + "uploads";
        }
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        return uploadPath;
    }

    /**
     * 파일 업로드 처리
     */
    public static List<FileDO> uploadFiles(HttpServletRequest req) throws ServletException, IOException {
        List<FileDO> fileList = new ArrayList<>();
        String uploadPath = getUploadPath(req);
        
        Collection<Part> parts = req.getParts();
        for (Part part : parts) {
            String fileName = getFileName(part);
            if (fileName != null && !fileName.isEmpty()) {
                String now = new SimpleDateFormat("yyyyMMdd_HmsS").format(new Date());
                String ext = fileName.substring(fileName.lastIndexOf("."));
                String newFileName = now + ext;

                part.write(uploadPath + File.separator + newFileName);

                FileDO fileDto = new FileDO();
                fileDto.setOriginal_file_name(fileName);
                fileDto.setStored_file_name(newFileName);
                fileDto.setFile_size(part.getSize());
                fileList.add(fileDto);
            }
        }
        return fileList;
    }

    private static String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    /**
     * 파일 다운로드 처리
     */
    public static void download(HttpServletRequest req, HttpServletResponse resp, String storedFileName, String originalFileName) {
        String uploadPath = getUploadPath(req);
        File file = new File(uploadPath, storedFileName);

        try (InputStream in = new FileInputStream(file);
             OutputStream out = resp.getOutputStream()) {

            resp.reset();
            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + new String(originalFileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");
            resp.setHeader("Content-Length", String.valueOf(file.length()));

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 서버에 저장된 파일 삭제
     */
    public static void deleteFile(HttpServletRequest req, String filename) {
        String uploadPath = getUploadPath(req);
        Path filePath = Paths.get(uploadPath, filename);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.out.println("파일 삭제 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 저장될 파일명 생성 (중복 방지)
     */
    private static String createStoredFileName(String originalFileName) {
        // UUID + 타임스탬프 + 확장자
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFileName.substring(dotIndex);
        }
        return UUID.randomUUID().toString() + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + extension;
    }
} 